// CommunityCommentService.java 完整接口
package com.anime.shop.service;

import com.anime.shop.common.Result;
import com.anime.shop.controller.community.CommunityCommentAddDTO;
import com.anime.shop.controller.community.CommunityCommentVO;
import com.anime.shop.entity.CommunityComment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface CommunityCommentService extends IService<CommunityComment> {
    /**
     * 分页查询帖子评论（匹配Controller调用）
     */
    Page<CommunityCommentVO> getCommentList(Long postId, Integer pageNum, Integer pageSize, Long currentUserId, Long parentId);

    /**
     * 发布评论/回复（DTO转Entity）
     */

    /**
     * 新方法：支持管理员删除
     */
    boolean deleteComment(Long id, Long userId, String role);

    /**
     * 评论点赞/取消点赞
     */
    Result<Map<String, Object>> toggleLike(Long commentId, Long userId);

    List<Long> getUserLikedCommentIds(Long userId);

    Integer getCommentCount(Long postId);

    Long addComment(CommunityCommentAddDTO dto, Long userId);
}