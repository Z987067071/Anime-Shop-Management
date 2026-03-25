package com.anime.shop.enums;

public enum PayTypeEnum {
//    ALIPAY(1, "支付宝"),
//    WXPAY(2, "微信支付"),
    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    REFUNDING(2, "退款中"),
    REFUNDED(3, "已退款"),
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String desc;

    PayTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    public static PayTypeEnum getByCode(Integer code) {
        for (PayTypeEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
