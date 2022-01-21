package com.cqupt.community.controller;

import com.cqupt.community.entity.User;
import com.cqupt.community.service.LikeService;
import com.cqupt.community.util.HostHolder;
import com.cqupt.community.util.JsonResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path = "/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/doLike", method = RequestMethod.POST)
    @ResponseBody
    public String doLike(int entityType, int entityId) {
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType, entityId);
        int isLiked = likeService.isLiked(user.getId(), entityType, entityId);
        long likeCount = likeService.likeCount(entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("isLiked", isLiked);
        map.put("likeCount", likeCount);
        return JsonResponseUtils.toJsonResponse(200, null, map);
    }
}
