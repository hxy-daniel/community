package com.cqupt.community.service;

import com.cqupt.community.dao.MessageDao;
import com.cqupt.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;
    /**
     * 查询消息列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectConversations(int userId, int offset, int limit){
        return messageDao.selectConversations(userId, offset, limit);
    }

    /**
     * 查询未读消息数量
     * @param userId
     * @param conversationId 会话id，为空则是查询所有会话，否则查询某个会话的未读数量
     * @return
     */
    public int selectUnReadCount(int userId, String conversationId) {
        return messageDao.selectUnReadCount(userId, conversationId);
    }

    /**
     * 查询每个会话的消息总数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int selectLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    /**
     * 查询会话的总数量
     * @param userId
     * @return
     */
    public int selectConversationCount(int userId) {
        return messageDao.selectConversationCount(userId);
    }
}
