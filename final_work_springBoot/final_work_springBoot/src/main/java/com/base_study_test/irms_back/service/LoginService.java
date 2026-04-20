package com.base_study_test.irms_back.service;

import com.base_study_test.irms_back.entity.Login;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
public interface LoginService {
    /**
     * @param username
     * @return Login
     * @description: 根据用户名查询用户信息
     */
    Login selectByUsername(String username);

    /**
     * @param login
     * @return int
     * @description: 添加用户信息
     */
    boolean addLogin(Login login);

    /**
     * @description: 获得唯一管理员用户名密码
     */
    Login selectOneAdmin();

    /**
     * @param username
     * @return boolean
     * @description: 判断用户名是否存在
     */
    boolean isExistUsername(String username);
}
