package com.cqupt.community.controller;

import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.cqupt.community.entity.*;
import com.cqupt.community.event.EventProducer;
import com.cqupt.community.service.*;
import com.cqupt.community.util.*;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.util.*;

/**
 * 帖子Controller
 */
@Controller
@RequestMapping(path = "/discussPost")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService searchService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/addDiscussPost", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) throws IOException {
        // 判断用户是否登录
        User user = hostHolder.getUser();
        if (user == null) {
            return JsonResponseUtils.toJsonResponse(400, "请登录后再发布！");
        }
        // 过滤敏感词和HTML标签
        title = HtmlUtils.htmlEscape(title);
        title = sensitiveWordFilter.filter(title);

        content = HtmlUtils.htmlEscape(content);
        content = sensitiveWordFilter.filter(content);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPost.setUserId(user.getId());
        discussPostService.addDiscussPost(discussPost);
//        // 添加到Elasticsearch
//        searchService.saveDiscussPost(discussPost);
        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_DISCUSSPOST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, discussPost.getId());

        return JsonResponseUtils.toJsonResponse(200, "发布成功！");
    }

    /**
     * 查询帖子详情页
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
    public String selectDiscussPostById(@PathVariable("id") int id, Model model, Page page) {
        // 查询帖子
        DiscussPost discussPost = discussPostService.selectDiscussPostById(id);
        model.addAttribute("discussPost", discussPost);
        User user = userService.getUserById(discussPost.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("likeCount", likeService.likeCount(1, discussPost.getId()));
        model.addAttribute("isLiked", hostHolder.getUser() == null ? 0 : likeService.isLiked(hostHolder.getUser().getId(), 1, discussPost.getId()));
        // 处理评论和回复
        page.setPageSize(5);
        page.setPath("/discussPost/detail/" + id);
        page.setTotals(discussPost.getCommentCount());

        List<Map<String, Object>> commentList = new ArrayList<>();
        List<Comment> comments = commentService.selectComment(ENTITY_TYPE_DISCUSSPOST, id, page.getOffSet(), page.getPageSize());
        if (comments != null) {
            for(Comment comment : comments) {
                Map<String, Object> commentMap = new HashMap<>();
                // 评论
                commentMap.put("comment", comment);
                User commentUser = userService.getUserById(comment.getUserId());
                // 评论用户
                commentMap.put("user", commentUser);
                List<Comment> subComments = commentService.selectComment(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                List<Map<String, Object>> subCommentList = new ArrayList<>();
                if (subCommentList != null) {
                    for(Comment subComment : subComments) {
                        Map<String, Object> subCommentMap = new HashMap<>();
                        // 回复
                        subCommentMap.put("comment", subComment);
                        User subCommentUser = userService.getUserById(subComment.getUserId());
                        // 回复用户
                        subCommentMap.put("user", subCommentUser);
                        User targetUser = subComment.getTargetId() == 0 ? null : userService.getUserById(subComment.getTargetId());
                        // 回复目标用户
                        subCommentMap.put("targetUser", targetUser);
                        subCommentMap.put("likeCount", likeService.likeCount(2, subComment.getId()));
                        subCommentMap.put("isLiked", hostHolder.getUser() == null ? 0 : likeService.isLiked(hostHolder.getUser().getId(), 2, subComment.getId()));
                        subCommentList.add(subCommentMap);
                    }
                }
                commentMap.put("subCommentList", subCommentList);
                // 回复数量
                int replyCount = commentService.getCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("replyCount", replyCount);
                commentMap.put("likeCount", likeService.likeCount(2, comment.getId()));
                commentMap.put("isLiked", hostHolder.getUser() == null ? 0 : likeService.isLiked(hostHolder.getUser().getId(), 2, comment.getId()));
                commentList.add(commentMap);
            }
        }


        model.addAttribute("commentList", commentList);
        return "/site/discuss-detail";
    }

    // 置顶
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_DISCUSSPOST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return JsonResponseUtils.toJsonResponse(200);
    }

    // 加精
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_DISCUSSPOST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return JsonResponseUtils.toJsonResponse(200);
    }

    // 删除
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);

        // 触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_DISCUSSPOST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return JsonResponseUtils.toJsonResponse(200);
    }
}
