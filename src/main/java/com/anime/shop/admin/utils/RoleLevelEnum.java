package com.anime.shop.admin.utils;

import lombok.Getter;

@Getter
public enum RoleLevelEnum {
    ADMIN("admin", 0),    // 最高权限
    MANAGER("manager", 1),// 管理员
    LEADER("leader", 2),   // 负责人
    MEMBER("member", 3),   // 普通成员
    CONSUMER("consumer", 4);// 普通用户（后台无权限）

    private final String role;  // 角色标识
    private final int level;    // 权限等级（数值越小权限越高）

    RoleLevelEnum(String role, int level) {
        this.role = role;
        this.level = level;
    }

    /**
     * 根据角色标识获取枚举（找不到返回null）
     */
    public static RoleLevelEnum getByRole(String role) {
        for (RoleLevelEnum e : values()) {
            if (e.getRole().equals(role)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 判断当前角色是否能编辑目标角色（当前角色等级 < 目标角色等级 才能编辑）
     */
    public boolean canEdit(RoleLevelEnum targetRole) {
        if (targetRole == null) {
            return false;
        }
        // 权限等级更小（权限更高）才能编辑，同级/更大（权限更低）不能编辑
        return this.getLevel() < targetRole.getLevel();
    }
}
