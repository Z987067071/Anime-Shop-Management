package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("u_user_address")
public class UserAddressEntity {
    @TableId(type = IdType.AUTO)
    private Long id; // 地址ID

    private Long userId; // 用户ID

    private String consignee; // 收货人

    private String phone; // 手机号

    private String province; // 省

    private String city; // 市

    private String district; // 区/县

    private String detailAddress; // 详细地址

    private Integer isDefault; // 是否默认地址

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableLogic // 逻辑删除标记
    private Integer isDelete; // 是否删除
}