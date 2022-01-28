package com.cqupt.community.controller;

import com.cqupt.community.annotation.LoginRequired;
import com.cqupt.community.entity.Event;
import com.cqupt.community.entity.User;
import com.cqupt.community.event.EventProducer;
import com.cqupt.community.service.LikeService;
import com.cqupt.community.util.CommunityConstant;
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
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/doLike", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String doLike(int entityType, int entityId, int targetUserId, int postId) {
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType, entityId, targetUserId);
        int isLiked = (user == null) ? 0 : likeService.isLiked(user.getId(), entityType, entityId);
        long likeCount = likeService.likeCount(entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("isLiked", isLiked);
        map.put("likeCount", likeCount);

        // 触发点赞事件
        if (isLiked == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(targetUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        return JsonResponseUtils.toJsonResponse(200, null, map);
    }
}
