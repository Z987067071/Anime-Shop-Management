package com.anime.shop.controller.dto.productdetail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ProductCommentAddDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;         // 商品ID
    @NotNull(message = "评论内容不能为空")
    @Length(max = 500, message = "根评论内容最多500字")
    private String content;         // 评论内容
    private Long parentId = 0L;     // 父评论ID（默认0）
    // 回复时内容长度校验（后端逻辑里处理）
    @Pattern(regexp = "^$|^([^,]+,)*[^,]+$", message = "图片URL格式错误（逗号分隔）")
    private String imageUrls = "";  // 评论图片URL（仅根评论可传）
}
