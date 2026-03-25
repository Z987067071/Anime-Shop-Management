package com.anime.shop.mapper;

import com.anime.shop.entity.UserAddressEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 地址Mapper（纯注解，无XML）
 */
public interface AddressMapper extends BaseMapper<UserAddressEntity> {

    /**
     * 查询用户未删除的地址列表
     */
    @Select("SELECT * FROM u_user_address WHERE user_id = #{userId} AND is_delete = 0 ORDER BY is_default DESC")
    List<UserAddressEntity> selectValidAddressByUserId(@Param("userId") Long userId);

    /**
     * 查询用户未删除的默认地址
     */
    @Select("SELECT * FROM u_user_address WHERE user_id = #{userId} AND is_default = 1 AND is_delete = 0 LIMIT 1")
    UserAddressEntity selectDefaultAddress(@Param("userId") Long userId);

    /**
     * 取消用户所有默认地址
     */
    @Update("UPDATE u_user_address SET is_default = 0 WHERE user_id = #{userId} AND is_delete = 0")
    void cancelAllDefault(@Param("userId") Long userId);

    /**
     * 根据ID查询地址（包含已删除的，供校验）
     */
    @Select("SELECT * FROM u_user_address WHERE id = #{id}")
    UserAddressEntity selectAddressById(@Param("id") Long id);

    /**
     * 逻辑删除地址（直接写更新SQL，避免MyBatis-Plus字段映射问题）
     */
    @Update("UPDATE u_user_address SET is_delete = 1 WHERE id = #{id} AND user_id = #{userId}")
    int logicDeleteAddress(@Param("id") Long id, @Param("userId") Long userId);
}