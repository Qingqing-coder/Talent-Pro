package com.base_study_test.irms_back.controller;

import com.base_study_test.irms_back.common.Result;
import com.base_study_test.irms_back.entity.Login;
import com.base_study_test.irms_back.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
@Tag(name = "注册信息管理")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "根据账号用户名查询注册信息")
    @GetMapping("/selectByUsername")
    public Result<Login> selectByUsername(String username) {
        Login login = loginService.selectByUsername(username);
        if (login == null) {
            return Result.error("用户不存在");
        }
        return Result.success(login);
    }

    @Operation(summary = "添加注册信息")
    @PostMapping("/add")
    public Result<String> addLogin(@RequestBody Login login) {
        if (loginService.addLogin(login)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @Operation(summary = "获得管理员信息")
    @GetMapping("/selectOneAdmin")
    public Result<Login> selectOneAdmin() {
        Login login = loginService.selectOneAdmin();
        if (login == null) {
            return Result.error("管理员不存在");
        }
        return Result.success(login);
    }

    @Operation(summary = "判断用户名是否存在")
    @GetMapping("/isExistUsername")
    public Result<Boolean> isExistUsername(String username) {
        return Result.success(loginService.isExistUsername(username));
    }
}
