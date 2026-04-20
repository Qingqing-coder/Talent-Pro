package com.base_study_test.irms_back.controller;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-19
 */
import com.base_study_test.irms_back.common.Result;
import com.base_study_test.irms_back.entity.Message;
import com.base_study_test.irms_back.common.WebSocketConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天信息管理")
@CrossOrigin
@RestController
@RequestMapping("/chat")
public class MessageController {

    private final WebSocketConfig webSocketConfig;

    public MessageController(WebSocketConfig webSocketConfig) {
        this.webSocketConfig = webSocketConfig;
    }

    @Operation(summary = "查询两个用户之间的历史消息")
    @GetMapping("/history")
    public Result<List<Message>> selectHistoryMessages(@RequestParam String from, @RequestParam String to) {
        List<Message> messages = webSocketConfig.selectHistoryMessages(from, to);
        if (messages == null || messages.isEmpty()) {
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(messages);
    }

    @Operation(summary = "根据发送者查询消息")
    @GetMapping("/selectByFrom")
    public Result<List<Message>> selectByFrom(@RequestParam String from) {
        List<Message> messages = webSocketConfig.selectMessagesByFrom(from);
        if (messages == null || messages.isEmpty()) {
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(messages);
    }

    @Operation(summary = "根据接收者查询消息")
    @GetMapping("/selectByTo")
    public Result<List<Message>> selectByTo(@RequestParam String to) {
        List<Message> messages = webSocketConfig.selectMessagesByTo(to);
        if (messages == null || messages.isEmpty()) {
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(messages);
    }
}