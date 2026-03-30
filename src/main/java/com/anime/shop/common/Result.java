package com.anime.shop.common;

import lombok.Data;

/**
 * 统一响应包装，前端只认 {code, msg, data}
 */
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> build(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(int code, String msg) {
        return build(code, msg, null);
    }

    public static <T> Result<T> error(ResultCode rc) {
        return build(rc.getCode(), rc.getMsg(), null);
    }

    // ---- 兼容旧调用，避免大范围改动 ----

    /** @deprecated 请使用 {@link #success(Object)} */
    @Deprecated
    public static <T> Result<T> ok(T data) {
        return success(data);
    }

    /** @deprecated 请使用 {@link #error(int, String)} */
    @Deprecated
    public static <T> Result<T> fail(String msg) {
        return error(ResultCode.ERROR.getCode(), msg);
    }

    /** @deprecated 请使用 {@link #build(int, String, Object)} */
    @Deprecated
    public static <T> Result<T> build(ResultCode rc, T data) {
        return build(rc.getCode(), rc.getMsg(), data);
    }
}
