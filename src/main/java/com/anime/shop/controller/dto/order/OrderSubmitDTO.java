package com.anime.shop.controller.dto.order;

import com.anime.shop.controller.dto.buyer.BuyerInfoDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderSubmitDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;                 // 用户ID

    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;      // 订单总金额

    @NotNull(message = "实付金额不能为空")
    private BigDecimal payAmount;        // 实付金额

    private BigDecimal freightAmount = BigDecimal.ZERO; // 运费
    private BigDecimal discountAmount = BigDecimal.ZERO; // 优惠金额
    @NotBlank(message = "收货人姓名不能为空",groups = {NormalOrderGroup.class})
    private String consignee;            // 收货人姓名

    @NotBlank(message = "收货人手机号不能为空", groups = {NormalOrderGroup.class})
    private String consigneePhone;

    @NotBlank(message = "收货人地址不能为空", groups = {NormalOrderGroup.class})
    private String consigneeAddress;

    private String remark;               // 订单备注

    @NotNull(message = "订单项不能为空")
    private List<OrderItemDTO> orderItems; // 订单项列表

    @NotNull(message = "是否票务订单标识不能为空")
    private Integer isTicket; // 是否票务订单：0=普通商品 1=漫展票务

    @NotNull(message = "票务订单必须填写购票人信息", groups = {TicketOrderGroup.class})
    private List<BuyerInfoDTO> buyerList;

    public interface NormalOrderGroup {}
    public interface TicketOrderGroup {}
}

