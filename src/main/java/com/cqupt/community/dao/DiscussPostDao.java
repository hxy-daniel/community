package com.cqupt.community.dao;

import com.cqupt.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子Dao层
 */
@Mapper
public interface DiscussPostDao {
    /**
     * 获取帖子
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> getDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * 获取总帖子数
     * @param userId
     * @return
     */
    public int getTotals(@Param("userId") int userId);

    /**
     *
     * @return
     */
//    public DiscussPost getOneDiscussPost();

    /**
     * 发布帖子
     * @param discussPost
     * @return
     */
    public int addDiscussPost(DiscussPost discussPost);

    public DiscussPost selectDiscussPostById(int id);

    public int updateCommentCount(int id, int commentCount);

    public int updateType(int id, int type);

    public int updateStatus(int id, int status);

    public int updateScore(int id, double score);
}
