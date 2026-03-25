package com.anime.shop.common;

import lombok.Getter;
// 业务异常抛出器
//作用：区分 “预期内业务异常” 与 “系统异常”
//场景：参数校验失败、订单不存在等“已知错误”抛它；后台日志不打堆栈，用户提示友好。
@Getter
public class BizException extends RuntimeException {
    private final int code;

    // 旧代码兼容
    public BizException(ResultCode rc) {
        super(rc.getMsg());
        this.code = rc.getCode();
    }

    //新
    public BizException(ResultCode rc, String customMsg) {
        super(customMsg); // 异常消息替换为自定义内容
        this.code = rc.getCode(); // 保留枚举的code值，不改动原有编码规则
    }
}
