package com.base_study_test.irms_back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Data
@Schema(name="公司信息")
public class Company {
    @Schema(description = "公司id")
    private Integer id;

    @Schema(description = "公司账号")
    private String username;

    @Schema(description = "公司名称")
    private String nickname;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "公司简介")
    private String companyIntroduction;
}
