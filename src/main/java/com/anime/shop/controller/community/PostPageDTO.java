package com.anime.shop.controller.community;

import lombok.Data;

@Data
public class PostPageDTO {
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 10;

    /**
     * 帖子状态（1=正常，默认查正常）
     */
    private Integer status = 1;

    /**
     * 用户ID（可选，查指定用户的帖子）
     */
    private Long userId;
}