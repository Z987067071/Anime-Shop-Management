package com.anime.shop.mapper.impl;

import com.anime.shop.controller.community.PostDTO;
import com.anime.shop.controller.community.PostDetailDTO;
import com.anime.shop.controller.community.PostPageDTO;
import com.anime.shop.entity.CommunityPost;
import com.anime.shop.mapper.CommunityPostMapper;
import com.anime.shop.service.CommunityPostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class CommunityPostServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost>
        implements CommunityPostService {

    @Resource
    private CommunityPostMapper postMapper;

    @Override
    public IPage<PostDTO> getPostList(PostPageDTO pageDTO) {
        // 分页对象（默认查正常帖子：status=1）
        Page<PostDTO> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        return postMapper.selectPostList(page, pageDTO.getStatus() == null ? 1 : pageDTO.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 事务保证
    public boolean publishPost(PostDTO postDTO) {
        // 1. 校验核心参数（标题/内容不能为空）
        if (postDTO.getTitle() == null || postDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("帖子标题不能为空");
        }
        if (postDTO.getContent() == null || postDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("帖子内容不能为空");
        }
        if (postDTO.getUserId() == null) {
            throw new IllegalArgumentException("发帖用户ID不能为空");
        }
        if (postDTO.getUserAvatar() == null) {
            postDTO.setUserAvatar("");
        }
        if (postDTO.getUserName() == null) {
            throw new IllegalArgumentException("发帖用户名不能为空");
        }

        // 2. DTO转Entity（严格匹配p_community_post表字段）
        CommunityPost post = new CommunityPost();
        BeanUtils.copyProperties(postDTO, post);

        // 3. 处理图片URL（前端传数组则转逗号分隔，匹配image_urls字段）
        if (postDTO.getImageUrlsList() != null && !postDTO.getImageUrlsList().isEmpty()) {
            post.setImageUrls(String.join(",", postDTO.getImageUrlsList()));
        } else {
            post.setImageUrls(""); // 无图片则设为空字符串
        }

        // 4. 初始化默认值（匹配表字段默认值）
        post.setLikeCount(0);       // 初始点赞数0
        post.setCommentCount(0);    // 初始评论数0
        post.setViewCount(0);       // 初始浏览数0
        post.setStatus(1);          // 状态默认1=正常
        // create_time/update_time由数据库自动生成，无需手动赋值

        // 5. 插入数据库（MyBatis-Plus自带方法）
        return save(post);
    }

    @Override
    public PostDetailDTO getPostDetail(Long id) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("帖子ID不能为空且必须为正数");
        }

        // 2. 查询帖子基础信息
        PostDTO postDTO = postMapper.selectPostById(id);
        if (postDTO == null) {
            throw new RuntimeException("帖子不存在或已被删除");
        }

        // 3. 转换为详情DTO
        PostDetailDTO detailDTO = new PostDetailDTO();
        BeanUtils.copyProperties(postDTO, detailDTO);

        // 4. 处理图片URL：逗号分隔字符串 → 列表
        if (StringUtils.hasText(postDTO.getImageUrls())) {
            List<String> imageList = Arrays.asList(postDTO.getImageUrls().split(","));
            detailDTO.setImageUrlsList(imageList);
        } else {
            detailDTO.setImageUrlsList(List.of()); // 空列表
        }

        // 5. 浏览数+1
        CommunityPost post = new CommunityPost();
        post.setId(id);
        post.setViewCount(postDTO.getViewCount() + 1);
        updateById(post);

        return detailDTO;
    }
}