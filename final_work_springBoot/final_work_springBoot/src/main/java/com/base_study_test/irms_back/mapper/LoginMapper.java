package com.base_study_test.irms_back.mapper;

import com.base_study_test.irms_back.entity.Login;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
@Mapper
public interface LoginMapper {
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
    int addLogin(Login login);

    /**
     * @description: 获得唯一高级管理员用户名密码
     */
    Login selectOneAdmin();

    /**
     * @param username
     * @description: 验证用户名是否已经存在
     */
    boolean isExistUsername(String username);
}
