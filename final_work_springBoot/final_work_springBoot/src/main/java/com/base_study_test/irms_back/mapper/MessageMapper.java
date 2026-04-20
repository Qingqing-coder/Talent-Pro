package com.base_study_test.irms_back.mapper;

import com.base_study_test.irms_back.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-19
 */
@Component
@Mapper
public interface MessageMapper {
    /**
     * 添加消息
     */
    int addMessage(Message message);

    /**
     * 查询两个用户之间的历史消息
     */
    List<Message> selectHistoryMessages(String from, String to);

    /**
     *  根据发送者查询消息
     */
    List<Message> selectMessagesByFrom(String from);

    /**
     *  根据接收者查询消息
     */
    List<Message> selectMessagesByTo(String to);

}
