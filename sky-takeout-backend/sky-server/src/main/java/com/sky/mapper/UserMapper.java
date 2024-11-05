package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT id, openid, name, phone, sex, id_number, avatar, create_time FROM user WHERE openid = #{openId}")
    User selectByOpenId(String openId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO user (openid, name, phone, sex, id_number, avatar, create_time) VALUES (#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    int insert(User user);
}
