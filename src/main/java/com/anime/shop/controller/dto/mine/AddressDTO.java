package com.anime.shop.controller.dto.mine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id; // 编辑时传ID，新增时不传

    @NotNull(message = "用户ID不能为空")
    private Long userId; // 从Token解析，前端可不传

    @NotBlank(message = "收货人姓名不能为空")
    private String consignee; // 收货人

    @NotBlank(message = "手机号不能为空")
    private String phone; // 手机号

    @NotBlank(message = "省份不能为空")
    private String province; // 省

    @NotBlank(message = "城市不能为空")
    private String city; // 市

    @NotBlank(message = "区县不能为空")
    private String district; // 区/县

    @NotBlank(message = "详细地址不能为空")
    private String detailAddress; // 详细地址

    private Integer isDefault; // 是否默认：1=是，0=否
}
