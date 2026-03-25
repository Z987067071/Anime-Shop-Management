package com.anime.shop.mapper;

import com.anime.shop.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<UserEntity> {
    /**
     * 按ID更新用户密码（适配u_user表）
     */
    @Update("UPDATE u_user SET password = #{newPassword}, updated_at = NOW() WHERE id = #{id}")
    int updatePasswordById(@Param("id") Long id, @Param("newPassword") String newPassword);

    @Update("UPDATE u_user SET avatar = #{avatarUrl}, updated_at = NOW() WHERE id = #{userId}")
    int updateAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);
}