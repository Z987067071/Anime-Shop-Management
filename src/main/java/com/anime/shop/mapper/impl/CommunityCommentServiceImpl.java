package com.anime.shop.mapper.impl;

import com.anime.shop.common.Result;
import com.anime.shop.controller.community.CommunityCommentAddDTO;
import com.anime.shop.controller.community.CommunityCommentVO;
import com.anime.shop.entity.CommunityComment;
import com.anime.shop.entity.CommunityCommentLike;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.CommunityCommentLikeMapper;
import com.anime.shop.mapper.CommunityCommentMapper;
import com.anime.shop.mapper.CommunityPostMapper;
import com.anime.shop.mapper.UserMapper;
import com.anime.shop.service.CommunityCommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommunityCommentServiceImpl extends ServiceImpl<CommunityCommentMapper, CommunityComment>
        implements CommunityCommentService {

    @Resource
    private CommunityCommentMapper communityCommentMapper;

    @Resource
    private CommunityCommentLikeMapper communityCommentLikeMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CommunityPostMapper communityPostMapper;




    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(CommunityCommentAddDTO dto, Long userId) {
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
        CommunityComment comment = new CommunityComment();
        comment.setPostId(dto.getPostId());
        comment.setUserId(userId);
        comment.setParentId(dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setImageUrls(dto.getImageUrls());
        UserEntity user = userMapper.selectById(userId);
        if (user != null) {
            comment.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            comment.setUserAvatar(user.getAvatar());
        }

        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setIsDelete(0);

        // 3. 插入数据库
        communityCommentMapper.insert(comment);

        communityPostMapper.incrCommentCount(dto.getPostId());

        return comment.getId();
    }
    /**
     * 分页查询帖子评论（返回IPage<CommentDTO>，匹配接口定义）
     */
    @Override
    public Page<CommunityCommentVO> getCommentList(Long postId, Integer pageNum, Integer pageSize, Long currentUserId, Long parentId) {
        // 1. 分页查询（根据parentId区分根评论/回复）
        Page<CommunityComment> page = new Page<>(pageNum, pageSize);
        IPage<CommunityComment> commentPage = communityCommentMapper.selectPage(page,
                Wrappers.<CommunityComment>lambdaQuery()
                        .eq(CommunityComment::getPostId, postId)
                        .eq(CommunityComment::getParentId, parentId) // 关键：过滤parentId
                        .eq(CommunityComment::getIsDelete, 0)
                        .orderByDesc(CommunityComment::getCreateTime));

        // 2. 转换为VO（根评论需要嵌套回复，回复不需要）
        List<CommunityCommentVO> commentVOList = commentPage.getRecords().stream().map(comment -> {
            CommunityCommentVO vo = convertToVO(comment, currentUserId);
            // 只有根评论（parentId=0）才查询它的回复
            if (parentId == 0) {
                List<CommunityComment> replyList = communityCommentMapper.selectReplyComments(comment.getId());
                vo.setReplyList(replyList.stream()
                        .map(reply -> convertToVO(reply, currentUserId))
                        .collect(Collectors.toList()));
            }
            return vo;
        }).collect(Collectors.toList());

        // 3. 封装分页结果
        Page<CommunityCommentVO> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setRecords(commentVOList);
        resultPage.setTotal(commentPage.getTotal());
        return resultPage;
    }

    /**
     * 新方法：支持管理员删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long id, Long userId, String role) {
        // 1. 查询评论
        CommunityComment comment = getById(id);
        if (comment == null || comment.getIsDelete() == 1) {
            return false;
        }

        // 2. 权限校验
        boolean isOwner = comment.getUserId().equals(userId);
        boolean isAdmin = "ADMIN".equals(role);
        if (!isOwner && !isAdmin) {
            return false;
        }

        // 3. 判断是否是根评论（parentId=0）
        int deleteCount = 1;
        Long postId = comment.getPostId();

        if (comment.getParentId() == 0) {
            deleteCount = communityCommentMapper.countRootCommentAndReplies(postId, comment.getId());

            communityCommentMapper.update(
                    null,
                    Wrappers.<CommunityComment>lambdaUpdate()
                            .eq(CommunityComment::getPostId, postId)
                            .and(w -> w.eq(CommunityComment::getId, comment.getId())
                                    .or().eq(CommunityComment::getParentId, comment.getId()))
                            .set(CommunityComment::getIsDelete, 1)
                            .set(CommunityComment::getUpdateTime, LocalDateTime.now())
            );
        } else {
            comment.setIsDelete(1);
            comment.setUpdateTime(LocalDateTime.now());
            updateById(comment);
        }

        communityPostMapper.decrCommentCountByNum(postId, deleteCount);

        return true;
    }


    /**
     * 点赞/取消点赞（返回最新状态）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> toggleLike(Long commentId, Long userId) {
        CommunityComment comment = communityCommentMapper.selectById(commentId);
        if (comment == null || comment.getIsDelete() == 1) {
            return Result.fail("评论不存在或已删除");
        }

        int count = communityCommentLikeMapper.countByCommentIdAndUserId(commentId, userId);
        boolean isLiked;
        int likeCount;

        if (count > 0) {
            // 取消点赞
            communityCommentLikeMapper.delete(
                    Wrappers.<CommunityCommentLike>lambdaQuery()
                            .eq(CommunityCommentLike::getCommunityCommentId, commentId)
                            .eq(CommunityCommentLike::getUserId, userId)
            );
            communityCommentMapper.decrLikeCount(commentId);
            isLiked = false;
        } else {
            // 新增点赞
            CommunityCommentLike like = new CommunityCommentLike();
            like.setCommunityCommentId(commentId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            like.setUpdateTime(LocalDateTime.now());
            communityCommentLikeMapper.insert(like);
            communityCommentMapper.incrLikeCount(commentId);
            isLiked = true;
        }

        // 查询最新点赞数
        CommunityComment updatedComment = communityCommentMapper.selectById(commentId);
        likeCount = updatedComment.getLikeCount();

        // 返回最新状态和点赞数
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", likeCount);
        return Result.success(result);
    }

    @Override
    public List<Long> getUserLikedCommentIds(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        try {
            List<CommunityCommentLike> likeList = communityCommentLikeMapper.selectList(
                    Wrappers.<CommunityCommentLike>lambdaQuery()
                            .eq(CommunityCommentLike::getUserId, userId)
                            .select(CommunityCommentLike::getCommunityCommentId)
            );
            return likeList.stream()
                    .map(CommunityCommentLike::getCommunityCommentId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Integer getCommentCount(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("帖子ID不能为空");
        }
        Integer count = communityCommentMapper.selectCommentCountByPostId(postId);
        return count == null ? 0 : count;
    }

    private CommunityCommentVO convertToVO(CommunityComment comment, Long currentUserId) {
        CommunityCommentVO vo = new CommunityCommentVO();
        BeanUtils.copyProperties(comment, vo);

        // 拆分图片URL为列表
        if (StringUtils.isNotBlank(comment.getImageUrls())) {
            vo.setImageUrls(Arrays.asList(comment.getImageUrls().split(",")));
        } else {
            vo.setImageUrls(new ArrayList<>());
        }

        // 联用户表查昵称/头像
        UserEntity user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUserName(StringUtils.isNotBlank(comment.getUserName()) ? comment.getUserName() : "匿名用户");
            vo.setUserAvatar(StringUtils.isNotBlank(comment.getUserAvatar()) ? comment.getUserAvatar() : "https://默认头像地址.png");
        } else {
            vo.setUserName("匿名用户");
            vo.setUserAvatar("https://默认头像地址.png");
        }

        // 判断当前用户是否点赞（未登录则为false）
        vo.setIsLiked(currentUserId != null && communityCommentLikeMapper.countByCommentIdAndUserId(comment.getId(), currentUserId) > 0);

        return vo;
    }
}