package com.anime.shop.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // 关键：不能漏这个注解！
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 新增时填充 create_time 和 update_time
    @Override
    public void insertFill(MetaObject metaObject) {
        // 注意：字段名要和实体类中的驼峰名称一致（createTime），不是数据库的下划线（create_time）
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    // 修改时填充 update_time
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
