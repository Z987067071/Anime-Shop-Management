package com.anime.shop.controller.dto.buyer;

import com.anime.shop.controller.dto.order.OrderSubmitDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BuyerInfoDTO {
    @NotBlank(message = "购票人ID不能为空", groups = {OrderSubmitDTO.TicketOrderGroup.class})
    private String id;

    @NotBlank(message = "购票人姓名不能为空", groups = {OrderSubmitDTO.TicketOrderGroup.class})
    private String name;

    @NotBlank(message = "购票人身份证号不能为空", groups = {OrderSubmitDTO.TicketOrderGroup.class})
    @Pattern(regexp = "^\\d{6}(\\*|\\d){8}\\d{4}$", message = "身份证号格式不正确（支持脱敏：441622********6645）", groups = {OrderSubmitDTO.TicketOrderGroup.class})
    private String idCard;
}
