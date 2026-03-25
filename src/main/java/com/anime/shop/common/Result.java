package com.anime.shop.common;

import lombok.Data;
// 统一外包装
//前端只认 {code, msg, data}，不管成功失败都走这个对象；全局异常处理器也用它组装错误 JSON。
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // 旧
    public static <T> Result<T> ok(T data) {return build(ResultCode.SUCCESS, data);}
    public static <T> Result<T> fail(String msg) {
        return build(ResultCode.ERROR.getCode(), msg, null);
    }
    public static <T> Result<T> build(ResultCode rc, T data) {
        return build(rc.getCode(), rc.getMsg(), data);
    }
    public static <T> Result<T> build(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // 新 兼容
    public static <T> Result<T> success() {
        return build(ResultCode.SUCCESS, null);
    }
    public static <T> Result<T> success(T data) {
        return ok(data); // 直接复用原有 ok()，避免重复逻辑
    }
    public static <T> Result<T> error(ResultCode rc) {
        return build(rc.getCode(), rc.getMsg(), null);
    }
    /**
     * 带自定义code+msg的错误返回（更灵活的错误提示）
     */
    public static <T> Result<T> error(int code, String msg) {
        return build(code, msg, null);
    }
}