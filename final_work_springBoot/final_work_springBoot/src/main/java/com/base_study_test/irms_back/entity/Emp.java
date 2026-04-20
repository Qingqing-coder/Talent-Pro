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
@Schema(name="求职者信息")
public class Emp {
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "期望薪资（千每月")
    private Integer salaryWishK;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "是否拥有经验")
    private  Integer hasExperience;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "学历")
    private String eduBack;

    @Schema(description = "简介")
    private String introduction;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "头像")
    private String photo;
}
