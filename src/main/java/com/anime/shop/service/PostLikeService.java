package com.anime.shop.service;

import com.anime.shop.controller.community.PostLikeDTO;
import com.anime.shop.entity.CommunityPost;
import com.anime.shop.entity.PostLike;
import com.anime.shop.mapper.CommunityPostMapper;
import com.anime.shop.mapper.PostLikeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private CommunityPostMapper communityPostMapper;

    /**
     * 点赞/取消点赞核心方法（修复唯一索引冲突）
     * @param postId 帖子ID
     * @param userId 点赞用户ID
     * @return 点赞状态+最新点赞数
     */
    @Transactional(rollbackFor = Exception.class)
    public PostLikeDTO toggleLike(Long postId, Long userId) {
        PostLikeDTO result = new PostLikeDTO();
        result.setCode(0); // 默认成功
        result.setMsg("操作成功");

        // 1. 校验帖子是否存在
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post == null) {
            result.setCode(1);
            result.setMsg("帖子不存在");
            return result;
        }

        // 2. 查询用户所有的点赞记录（不管是否取消）
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);
        PostLike existLike = postLikeMapper.selectOne(queryWrapper);

        // 3. 处理点赞/取消点赞逻辑（核心修复：复用记录，避免重复插入）
        if (existLike == null) {
            // 情况1：无记录 → 新增点赞记录 + 点赞数+1
            PostLike newLike = new PostLike();
            newLike.setPostId(postId);
            newLike.setUserId(userId);
            newLike.setIsCancel(0); // 未取消
            postLikeMapper.insert(newLike);

            // 点赞数+1（防止负数）
            post.setLikeCount(Math.max(0, post.getLikeCount() + 1));
            result.setIsLiked(true);
            result.setMsg("点赞成功");
        } else {
            // 情况2：有记录 → 切换取消状态 + 更新点赞数
            boolean isCurrentlyCanceled = existLike.getIsCancel() == 1;
            if (isCurrentlyCanceled) {
                // 已取消 → 重新点赞：改回未取消 + 点赞数+1
                existLike.setIsCancel(0);
                post.setLikeCount(Math.max(0, post.getLikeCount() + 1));
                result.setIsLiked(true);
                result.setMsg("点赞成功");
            } else {
                // 未取消 → 取消点赞：改为取消 + 点赞数-1
                existLike.setIsCancel(1);
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                result.setIsLiked(false);
                result.setMsg("取消点赞");
            }
            postLikeMapper.updateById(existLike);
        }

        // 4. 更新帖子表的点赞数
        communityPostMapper.updateById(post);

        // 5. 返回最新状态
        result.setLikeCount(post.getLikeCount());
        return result;
    }

    /**
     * 查询用户是否给帖子点过赞（修复：查所有记录，判断isCancel）
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return true=已点赞，false=未点赞
     */
    public Boolean isLiked(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId)
                .eq(PostLike::getIsCancel, 0); // 只查未取消的点赞
        return postLikeMapper.exists(queryWrapper);
    }
}
