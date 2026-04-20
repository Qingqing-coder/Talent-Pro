package com.base_study_test.irms_back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
@Data
@Schema(name="职位信息")
public class Job {
    @Schema(description = "职位id")
    private Integer id;

    @Schema(description = "职位名称")
    private String title;

    @Schema(description = "薪水")
    private String salary;

    @Schema(description = "公司名字")
    private String companyName;

    @Schema(description = "公司规模")
    private String companySize;

    @Schema(description = "公司所在地")
    private String fullLocation;

    @Schema(description = "经验要求")
    private String experience;

    @Schema(description = "学历要求")
    private String education;

    @Schema(description = "职位类型")
    private String type;

    @Schema(description = "职位待遇福利")
    private String benefits;

    @Schema(description = "职位类型")
    private String jobType;

    @Schema(description = "发布时间")
    private Timestamp createTime;

    @Schema(description = "收藏量")
    private Integer preNum;
}


