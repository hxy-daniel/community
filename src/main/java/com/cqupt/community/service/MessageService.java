package com.cqupt.community.service;

import com.cqupt.community.dao.MessageDao;
import com.cqupt.community.entity.Message;
import com.cqupt.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

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

    /**
     * 查询会话详情
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectConversationDetail(String conversationId, int offset, int limit){
        List<Message> messages = messageDao.selectConversationDetail(conversationId, offset, limit);
        return messages;
    }

    /**
     * 设置已读
     * @param id
     * @return
     */
    public int readLetter(List<Integer> ids) {
        return messageDao.updateStatus(ids, 1);
    }

    /**
     * 新增消息
     * @param message
     * @return
     */
    public int insertLetter(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveWordFilter.filter(message.getContent()));
        return messageDao.insertLetter(message);
    }

    public Message findLatestNotice(int userId, String topic) {
        return messageDao.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageDao.selectNoticeCount(userId, topic);
    }

    public int findNoticeUnreadCount(int userId, String topic) {
        return messageDao.selectNoticeUnreadCount(userId, topic);
    }

    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageDao.selectNotices(userId, topic, offset, limit);
    }
}
