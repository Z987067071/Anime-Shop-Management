package com.anime.shop.service;

import com.anime.shop.controller.community.PostDTO;
import com.anime.shop.controller.community.PostDetailDTO;
import com.anime.shop.controller.community.PostPageDTO;
import com.anime.shop.entity.CommunityPost;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;


public interface CommunityPostService extends IService<CommunityPost> {
    // 社区帖子列表
    IPage<PostDTO> getPostList(PostPageDTO pageDTO);
    // 新增帖子
    boolean publishPost(PostDTO postDTO);
    // 查看帖子详情
    PostDetailDTO getPostDetail(Long id);

}