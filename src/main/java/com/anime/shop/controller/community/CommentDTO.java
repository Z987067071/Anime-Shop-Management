package com.anime.shop.controller.community;

import lombok.Data;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private List<String> imageUrls;
    private String userName;
    private String userAvatar;
    private Integer likeCount;
    private Boolean liked;
}