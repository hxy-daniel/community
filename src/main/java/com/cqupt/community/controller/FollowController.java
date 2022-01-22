package com.cqupt.community.controller;

import com.cqupt.community.service.FollowService;
import com.cqupt.community.util.HostHolder;
import com.cqupt.community.util.JsonResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        followService.follow(hostHolder.getUser().getId(), entityType, entityId);
        return JsonResponseUtils.toJsonResponse(200, "关注成功");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        followService.unfollow(hostHolder.getUser().getId(), entityType, entityId);
        return JsonResponseUtils.toJsonResponse(200, "取消关注成功");
    }
}
