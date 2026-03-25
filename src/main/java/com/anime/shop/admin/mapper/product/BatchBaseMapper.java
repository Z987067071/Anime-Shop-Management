package com.anime.shop.admin.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * 通用批量插入 Mapper
 * @param <T> 实体类型
 */
public interface BatchBaseMapper<T> extends BaseMapper<T> {
    /**
     * 批量插入（MyBatis-Plus 3.5.6 适配）
     */
    int insertBatchSomeColumn(Collection<T> entityList);
}
