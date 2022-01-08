package com.cqupt.community.service;

import com.cqupt.community.dao.DiscussPostDao;
import com.cqupt.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    DiscussPostDao discussPostDao;

    public List<DiscussPost> getDiscussPosts(int userId, int offset, int limit){
        return discussPostDao.getDiscussPosts(userId, offset, limit);
    }

    public int getTotals(int userId) {
        return discussPostDao.getTotals(userId);
    }
}
