package com.base_study_test.irms_back.service.impl;

import com.base_study_test.irms_back.entity.Login;
import com.base_study_test.irms_back.mapper.LoginMapper;
import com.base_study_test.irms_back.service.LoginService;
import org.springframework.stereotype.Service;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
@Service
public class LoginServiceImpl implements LoginService {
    private final LoginMapper loginMapper;

    public LoginServiceImpl(LoginMapper loginMapper) {
        this.loginMapper = loginMapper;
    }

    @Override
    public Login selectByUsername(String username) {
        return loginMapper.selectByUsername(username);
    }

    @Override
    public boolean addLogin(Login login) {
        return loginMapper.addLogin(login) > 0;
    }

    @Override
    public Login selectOneAdmin() {
        return loginMapper.selectOneAdmin();
    }

    @Override
    public boolean isExistUsername(String username) {
        return loginMapper.isExistUsername(username);
    }
}
