package com.anime.shop.admin.mapper.comic;

import com.anime.shop.entity.ComicCon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminComicConMapper extends BaseMapper<ComicCon> {
    /**
     * 根据漫展ID查询关联的productId
     */
    @Select("SELECT product_id FROM comic_con WHERE id = #{comicConId}")
    Long selectProductIdById(Long comicConId);
}
