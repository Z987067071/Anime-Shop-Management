package com.anime.shop.mapper.impl;

import com.anime.shop.controller.dto.productdetail.ProductCommentAddDTO;
import com.anime.shop.controller.dto.productdetail.ProductCommentVO;
import com.anime.shop.entity.CommentLike;
import com.anime.shop.entity.ProductComment;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.CommentLikeMapper;
import com.anime.shop.mapper.ProductCommentMapper;
import com.anime.shop.mapper.UserMapper;
import com.anime.shop.service.ProductCommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCommentServiceImpl implements ProductCommentService {

    private final ProductCommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final UserMapper userMapper; // 假设你有用户表Mapper

    /**
     * 新增评论（严格匹配你的表约束）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(ProductCommentAddDTO dto, Long userId) {
        // 1. 内容长度校验（根评论500字，回复300字）
        if (dto.getParentId() == 0) {
            // 根评论
            if (dto.getContent().length() > 500) {
                throw new IllegalArgumentException("根评论内容最多500字");
            }
        } else {
            // 回复
            if (dto.getContent().length() > 300) {
                throw new IllegalArgumentException("回复内容最多300字");
            }
            // 回复不能传图片
            if (StringUtils.isNotBlank(dto.getImageUrls())) {
                throw new IllegalArgumentException("回复不支持上传图片");
            }
        }

        // 2. 封装实体
        ProductComment comment = new ProductComment();
        comment.setProductId(dto.getProductId());
        comment.setUserId(userId);
        comment.setParentId(dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setImageUrls(dto.getImageUrls()); // 根评论才传，回复为空
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setIsDelete(0);

        // 3. 插入数据库
        commentMapper.insert(comment);
        return comment.getId();
    }

    /**
     * 获取评论列表（根评论分页，嵌套回复）
     */
    @Override
    public Page<ProductCommentVO> getCommentList(Long productId, Integer pageNum, Integer pageSize, Long currentUserId) {
        // 1. 分页查询根评论
        Page<ProductComment> page = new Page<>(pageNum, pageSize);
        IPage<ProductComment> rootPage = commentMapper.selectPage(page,
                Wrappers.<ProductComment>lambdaQuery()
                        .eq(ProductComment::getProductId, productId)
                        .eq(ProductComment::getParentId, 0)
                        .eq(ProductComment::getIsDelete, 0)
                        .orderByDesc(ProductComment::getCreateTime));

        // 2. 转换为VO并嵌套回复
        List<ProductCommentVO> productCommentVOList = rootPage.getRecords().stream().map(rootComment -> {
            ProductCommentVO vo = convertToVO(rootComment, currentUserId);
            // 查询当前根评论的回复列表
            List<ProductComment> replyList = commentMapper.selectReplyComments(rootComment.getId());
            vo.setReplyList(replyList.stream()
                    .map(reply -> convertToVO(reply, currentUserId))
                    .collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());

        // 3. 封装分页结果
        Page<ProductCommentVO> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setRecords(productCommentVOList);
        resultPage.setTotal(rootPage.getTotal());
        return resultPage;
    }

    /**
     * 点赞/取消点赞（利用唯一索引uk_comment_user防重复）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long commentId, Long userId) {
        // 1. 检查评论是否存在
        ProductComment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDelete() == 1) {
            throw new IllegalArgumentException("评论不存在或已删除");
        }

        // 2. 检查是否已点赞
        int count = commentLikeMapper.countByCommentIdAndUserId(commentId, userId);
        if (count > 0) {
            // 取消点赞
            commentLikeMapper.delete(
                    Wrappers.<CommentLike>lambdaQuery()
                            .eq(CommentLike::getCommentId, commentId)
                            .eq(CommentLike::getUserId, userId)
            );
            commentMapper.decrLikeCount(commentId);
        } else {
            // 新增点赞
            CommentLike like = new CommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            commentLikeMapper.insert(like);
            commentMapper.incrLikeCount(commentId);
        }
    }

    /**
     * 删除评论（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId, String role) {
        // 1. 检查评论
        ProductComment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDelete() == 1) {
            throw new IllegalArgumentException("评论不存在或已删除");
        }

        // 2. 权限校验（本人 或 管理员（ADMIN））
        boolean isOwner = comment.getUserId().equals(userId);
        boolean isAdmin = "ADMIN".equals(role); // 匹配你Jwt中存储的角色值
        if (!isOwner && !isAdmin) {
            throw new SecurityException("无权限删除该评论");
        }

        // 3. 逻辑删除
        comment.setIsDelete(1);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);
    }

    // 私有方法：转换为VO（拆分图片URL、联用户表）
    private ProductCommentVO convertToVO(ProductComment comment, Long currentUserId) {
        ProductCommentVO vo = new ProductCommentVO();
        BeanUtils.copyProperties(comment, vo);

        // 拆分图片URL为列表
        if (StringUtils.isNotBlank(comment.getImageUrls())) {
            vo.setImageUrls(Arrays.asList(comment.getImageUrls().split(",")));
        } else {
            vo.setImageUrls(new ArrayList<>());
        }

        // 联用户表查昵称/头像（适配你的UserEntity和UserMapper）
        UserEntity user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname()); // 匹配UserEntity的nickname字段
            vo.setUserAvatar(user.getAvatar()); // 匹配UserEntity的avatar字段
        } else {
            // 兜底：用户不存在时显示默认值
            vo.setUserName("匿名用户");
            vo.setUserAvatar("https://默认头像地址.png");
        }

        // 判断当前用户是否点赞
        vo.setIsLiked(commentLikeMapper.countByCommentIdAndUserId(comment.getId(), currentUserId) > 0);

        return vo;
    }

    @Override
    public Integer getCommentCount(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        Integer count = commentMapper.selectCommentCountByProductId(productId);
        return count == null ? 0 : count;
    }
}
