package com.anime.shop.controller.dto.buyer;

import lombok.Data;

@Data
public class BuyerVO {
    private Long id;
    private String name;
    private String idCard;
    private Integer auditStatus;
    private String auditRemark;
}