package com.cqupt.community.dao;

import com.cqupt.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostDao {
    public List<DiscussPost> getDiscussPosts(int userId, int offset, int limit);

    public int getTotals(@Param("userId") int userId);

    public DiscussPost getOneDiscussPost();
}
