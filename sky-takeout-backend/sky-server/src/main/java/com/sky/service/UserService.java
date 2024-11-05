package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {
    /**
     * 此方法用于：微信登录
     *
     * @param userLoginDTO 微信登录参数
     * @return User
     */
    User userLogin(UserLoginDTO userLoginDTO);
}
