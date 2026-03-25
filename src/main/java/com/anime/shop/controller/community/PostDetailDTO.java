package com.anime.shop.controller.community;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailDTO {
    private Long id;
    private Long userId;
    private String userAvatar;
    private String userName;
    private String title;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> imageUrlsList;
    private Integer status;
    private Boolean isLiked;
}
