package com.cqupt.community.dao;

import com.cqupt.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao {
    /**
     * 查询消息列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询未读消息数量
     * @param userId
     * @param conversationId 会话id，为空则是查询所有会话，否则查询某个会话的未读数量
     * @return
     */
    public int selectUnReadCount(int userId, String conversationId);

    /**
     * 查询每个会话的消息总数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int selectLetterCount(String conversationId);

    /**
     * 查询会话的总数量
     * @param userId
     * @return
     */
    public int selectConversationCount(int userId);


    /**
     * 查询会话详情
     * @param conversationId
     * @return
     */
    public List<Message> selectConversationDetail(String conversationId, int offset, int limit);

    /**
     * 发送私信
     * @param message
     * @return
     */
    public int insertLetter(Message message);

    /**
     * 设置消息为已读
     * @param id
     * @return
     */
    public int updateStatus(List<Integer> ids, int status);
}
