package com.base_study_test.irms_back.common;

import com.base_study_test.irms_back.entity.Message;
import com.base_study_test.irms_back.mapper.MessageMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket消息处理器，处理连接、消息收发、异常等
 */
public class EchoHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoHandler.class);

    // 存储连接中的会话，key：用户名（即Message.from或Message.to）
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();

    private final MessageMapper messageMapper;

    public EchoHandler(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从连接参数中获取用户标识，例如 ws://host:port/channel/echo?username=Tom
        String username = getUsernameFromSession(session);
        if (username != null) {
            sessions.put(username, session);
            LOGGER.info("WebSocket连接建立：{}（sessionId={}）", username, session.getId());
            session.sendMessage(new TextMessage("连接成功，当前用户：" + username));
        } else {
            LOGGER.warn("WebSocket连接失败：无法识别用户名");
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        LOGGER.info("[websocket] 收到消息：{}", textMessage.getPayload());

        // 将消息转为实体对象
        Message data = gson.fromJson(textMessage.getPayload(), Message.class);

        // 存入数据库
        messageMapper.addMessage(data);
        LOGGER.info("[websocket] 消息写入数据库成功：{}", data);

        // 推送给接收者
        WebSocketSession toSession = sessions.get(data.getTo());
        if (toSession != null && toSession.isOpen()) {
            toSession.sendMessage(new TextMessage(gson.toJson(data)));
        }

        // 推送给发送者本人（确认消息）
        session.sendMessage(new TextMessage(gson.toJson(data)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = getUsernameFromSession(session);
        if (username != null) {
            sessions.remove(username);
            LOGGER.info("WebSocket连接关闭：{}（sessionId={}）", username, session.getId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOGGER.error("WebSocket异常（sessionId={}）：{}", session.getId(), exception.getMessage());
        session.close();
        String username = getUsernameFromSession(session);
        if (username != null) {
            sessions.remove(username);
        }
    }

    /**
     * 从 session 的 URL 参数中提取用户名
     * 例如：ws://localhost:8080/channel/echo?username=Tom
     */
    private String getUsernameFromSession(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query != null && query.startsWith("username=")) {
            return query.split("=")[1];
        }
        return null;
    }

    // 定时发送 Ping（每 30 秒一次）
    @Scheduled(fixedRate = 3000)
    public void sendPingToAll() {
        sessions.forEach((username, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new PingMessage());
                    LOGGER.debug("Sent Ping to {}", username);
                } catch (IOException e) {
                    LOGGER.error("Ping failed for {}: {}", username, e.getMessage());
                    sessions.remove(username); // 清理失效会话
                }
            }
        });
    }

    // 处理 Pong 响应
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        LOGGER.debug("Received Pong from {}", getUsernameFromSession(session));
    }
}
