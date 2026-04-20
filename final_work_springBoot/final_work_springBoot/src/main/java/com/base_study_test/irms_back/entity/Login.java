package com.base_study_test.irms_back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
@Data
@Schema(name="登录信息")
public class Login {
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户权限等级")
    private Integer role;
}
