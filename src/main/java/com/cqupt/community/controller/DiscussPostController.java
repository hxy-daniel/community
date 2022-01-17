package com.cqupt.community.controller;

import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.cqupt.community.entity.DiscussPost;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.DiscussPostService;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.HostHolder;
import com.cqupt.community.util.JsonResponseUtils;
import com.cqupt.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.Date;

/**
 * 帖子Controller
 */
@Controller
@RequestMapping(path = "/discussPost")
public class DiscussPostController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    private UserService userService;

    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/addDiscussPost", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
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
        discussPost.setUserId(String.valueOf(user.getId()));
        discussPostService.addDiscussPost(discussPost);
        return JsonResponseUtils.toJsonResponse(200, "发布成功！");
    }

    /**
     * 查询帖子详情页
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
    public String selectDiscussPostById(@PathVariable("id") int id, Model model) {
        DiscussPost discussPost = discussPostService.selectDiscussPostById(id);
        model.addAttribute("discussPost", discussPost);
        User user = userService.getUserById(Integer.parseInt(discussPost.getUserId()));
        model.addAttribute("user", user);
        return "/site/discuss-detail";
    }
}
