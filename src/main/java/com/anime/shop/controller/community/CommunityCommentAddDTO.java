package com.anime.shop.controller.community;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunityCommentAddDTO {
    @NotNull(message = "帖子ID不能为空")
    private Long postId;            // 社区帖子ID
    private Long parentId = 0L;     // 父评论ID（默认根评论）
    @NotBlank(message = "评论内容不能为空")
    private String content;         // 评论内容
    private String imageUrls;       // 图片URL（逗号分隔，仅根评论支持）
}
