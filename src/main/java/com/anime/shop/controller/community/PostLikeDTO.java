package com.anime.shop.controller.community;

import lombok.Data;

/**
 * 点赞操作返回结果
 */
@Data
public class PostLikeDTO {
    /**
     * 状态码：0=成功，1=失败
     */
    private Integer code = 0;

    /**
     * 提示信息
     */
    private String msg = "操作成功";

    /**
     * 是否点赞成功（true=已点赞，false=已取消）
     */
    private Boolean isLiked;

    /**
     * 最新点赞数
     */
    private Integer likeCount;
}