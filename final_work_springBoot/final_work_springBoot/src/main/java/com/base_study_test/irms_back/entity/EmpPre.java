package com.base_study_test.irms_back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-17
 */
@Data
@Schema(name="用户收藏工作表")
public class EmpPre {
    @Schema(description = "收藏关系id")
    private Integer preferenceId;

    @Schema(description = "用户名")
    private  String username;

    @Schema(description = "工作id")
    private Integer jobId;
}
