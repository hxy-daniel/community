package com.cqupt.community.service;

import com.cqupt.community.dao.CommentDao;
import com.cqupt.community.dao.DiscussPostDao;
import com.cqupt.community.entity.Comment;
import com.cqupt.community.entity.DiscussPost;
import com.cqupt.community.util.CommunityConstant;
import com.cqupt.community.util.HostHolder;
import com.cqupt.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class CommentService implements CommunityConstant {



    @Autowired
    private CommentDao commentDao;

    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    public List<Comment> selectComment(int entityType, int entityId, int offset, int limit) {
        return commentDao.selectComment(entityType, entityId, offset, limit);
    }

    public int getCommentCount(int entityType, int entityId){
        return commentDao.getCommentCount(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveWordFilter.filter(comment.getContent()));
        int rows = commentDao.addComment(comment);
        // 修改帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_DISCUSSPOST) {
            DiscussPost discussPost = discussPostDao.selectDiscussPostById(comment.getEntityId());
            discussPostDao.updateCommentCount(discussPost.getId(), discussPost.getCommentCount() + 1);
        }
        return rows;
    }
}
