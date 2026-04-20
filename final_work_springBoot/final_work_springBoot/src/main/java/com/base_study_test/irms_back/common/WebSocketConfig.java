package com.base_study_test.irms_back.common;

import com.base_study_test.irms_back.entity.Message;
import com.base_study_test.irms_back.mapper.MessageMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-19
 */

@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig  implements WebSocketConfigurer {

    private final MessageMapper messageMapper;

    public WebSocketConfig(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    /**
     * 查询历史消息
     */
    public List<Message> selectHistoryMessages(String from, String to) {
        return messageMapper.selectHistoryMessages(from, to);
    }

    /**
     * 根据发送者查询消息
     */
    public List<Message> selectMessagesByFrom(String from) {
        return messageMapper.selectMessagesByFrom(from);
    }

    /**
     * 根据接收者查询消息
     */
    public List<Message> selectMessagesByTo(String to) {
        return messageMapper.selectMessagesByTo(to);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new EchoHandler(messageMapper), "/channel/echo")
                .setAllowedOrigins("*");
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }
}