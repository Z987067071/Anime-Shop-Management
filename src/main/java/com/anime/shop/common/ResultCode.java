package com.anime.shop.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
// 错误号字典
@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(0, "success"),
    ERROR(1, "error"),
    PARAM_ERROR(400, "参数错误"),
    ROLE_PERMISSION_DENIED(403, "权限不足，无法编辑该角色"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器内部错误"),

    NO_TOKEN(401, "未携带登录Token"),
    TOKEN_INVALID(401, "Token无效或已篡改"),
    TOKEN_EXPIRED(401, "Token已过期，请重新登录"),
    NO_PERMISSION(403, "无访问权限，请联系管理员"),

    /* ===== 业务码 ===== */
    CAPTCHA_ERROR(10001, "验证码错误"),
    CAPTCHA_COOLDOWN(10005, "获取验证码过于频繁，请稍后再试"),
    USER_EXIST(10002, "账号已存在"),
    USER_NOT_FOUND(10003, "账号不存在"),
    USERNAME_OR_PWD_ERROR(10004, "账号或密码错误"),
    USER_DISABLED(10008, "账号已被禁用，请联系管理员"),
    PWD_RESET_NO_PERMISSION(10006, "无权限重置密码，仅管理员/经理可操作"),
    PWD_RESET_FAILED(10007, "密码重置失败"),

    CATEGORY_NULLFOUND(400, "父分类不存在"),
    CATEGORY_MAXTIPS(400,"最多支持3级分类，无法新增子分类"),
    CATEGORY_ALREADY_EXISTS(400,"同层级分类名称已存在"),
    CATEGORY_DELETE_FAIL(400, "分类删除失败"),

    PRODUCT_NOT_FOUND(400,"商品未找到");


    private final int code;
    private final String msg;
}