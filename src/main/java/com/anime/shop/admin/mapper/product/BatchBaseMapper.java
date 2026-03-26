package com.anime.shop.admin.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * 通用批量插入 Mapper
 * @param <T> 实体类型
 */
public interface BatchBaseMapper<T> extends BaseMapper<T> {
    int insertBatchSomeColumn(Collection<T> entityList);
}
