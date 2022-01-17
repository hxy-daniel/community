package com.cqupt.community.controller;

import com.cqupt.community.entity.Comment;
import com.cqupt.community.service.CommentService;
import com.cqupt.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(Comment comment, @PathVariable("discussPostId") int discussPostId){
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);

        return "redirect:/discussPost/detail/" + discussPostId;
    }
}
