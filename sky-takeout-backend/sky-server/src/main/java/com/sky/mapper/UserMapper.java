package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("SELECT id, openid, name, phone, sex, id_number, avatar, create_time FROM user WHERE openid = #{openId}")
    User selectByOpenId(String openId);

    @Select("SELECT id, openid, name, phone, sex, id_number, avatar, create_time FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO user (openid, name, phone, sex, id_number, avatar, create_time) VALUES (#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    int insert(User user);

    /**
     * 此方法用于：根据动态条件，统计用户数量
     * @param map 动态条件
     * @return 用户数量
     */
    Integer countByMap(Map<String, Object> map);
}
