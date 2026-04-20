package com.base_study_test.irms_back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-19
 */
@Data
@Schema(name="消息")
public class Message {
    @Schema(description = "消息id")
    private Integer id;


    @Schema(description = "发送者")
    private String from;

    @Schema(description = "接收者")
    private String to;

    @Schema(description = "发送时间")
    private Timestamp sendTime;

    @Schema(description = "消息内容")
    private String content;
}
