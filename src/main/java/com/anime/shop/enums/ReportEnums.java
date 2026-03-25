package com.anime.shop.enums;

import lombok.Getter;

/**
 * 举报相关枚举
 */
public class ReportEnums {

    /**
     * 举报目标类型
     */
    @Getter
    public enum TargetType {
        PRODUCT(1, "商品"),
        PRODUCT_COMMENT(2, "商品评论"),
        COMMUNITY_POST(3, "社区帖子"),
        COMMUNITY_POST_COMMENT(4, "社区帖子评论");

        private final int code;
        private final String desc;

        TargetType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 根据code获取枚举
        public static TargetType getByCode(int code) {
            for (TargetType type : values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 举报理由枚举
     */
    @Getter
    public enum ReportReason {
        ILLEGAL(1, "违反法律规定"),
        PORNOGRAPHY(2, "色情低俗"),
        GAMBLING_FRAUD(3, "赌博诈骗"),
        PERSONAL_ATTACK(4, "人身攻击"),
        PRIVACY_INVASION(5, "侵犯隐私"),
        SPAM_AD(6, "垃圾广告"),
        INCITE_QUARREL(7, "引战"),
        SPAM_POST(8, "刷屏/抢楼"),
        YOUTH_HARMFUL(9, "青少年不良"),
        RUMOR(10, "谣言造谣"),
        OTHER(99, "其他");

        private final int code;
        private final String desc;

        ReportReason(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 根据code获取枚举
        public static ReportReason getByCode(int code) {
            for (ReportReason reason : values()) {
                if (reason.getCode() == code) {
                    return reason;
                }
            }
            return null;
        }
    }

    /**
     * 举报处理状态
     */
    @Getter
    public enum ReportStatus {
        PENDING(0, "待处理"),
        ACCEPTED(1, "已受理"),
        REJECTED(2, "已驳回"),
        PROCESSED(3, "已处理");

        private final int code;
        private final String desc;

        ReportStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 根据code获取枚举
        public static ReportStatus getByCode(int code) {
            for (ReportStatus status : values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            return null;
        }
    }
}