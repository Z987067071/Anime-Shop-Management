package com.anime.shop.controller.dto.mine;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressVO {
    private Long id; // 地址ID
    private String consignee; // 收货人
    private String phone; // 手机号
    private String province; // 省
    private String city; // 市
    private String district; // 区/县
    private String detailAddress; // 详细地址
    private String fullAddress; // 省市区+详细地址（前端直接展示）
    private Integer isDefault; // 是否默认地址
    private LocalDateTime createTime; // 创建时间
}
