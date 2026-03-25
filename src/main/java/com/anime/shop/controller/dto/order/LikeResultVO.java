package com.anime.shop.controller.dto.order;

import lombok.Data;

@Data
public class LikeResultVO {
    /**
     * 是否点赞
     */
    private Boolean hasLiked;

    /**
     * 商品当前点赞数
     */
    private Integer likeCount;
}