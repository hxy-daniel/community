package com.cqupt.community.controller;

import com.cqupt.community.entity.DiscussPost;
import com.cqupt.community.entity.Page;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.DiscussPostService;
import com.cqupt.community.service.LikeService;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController implements CommunityConstant {

    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String showIndex(Model model, Page page) {
        page.setPath("/index");
        page.setTotals(discussPostService.getTotals(0));
        List<DiscussPost> list = discussPostService.getDiscussPosts(0, page.getOffSet(), page.getPageSize());
        List<Map<String,Object>> disscussPosts = new ArrayList<>();
        for (DiscussPost post : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            User user = userService.getUserById(post.getUserId());
            map.put("user", user);
            map.put("likeCount", likeService.likeCount(ENTITY_TYPE_DISCUSSPOST, post.getId()));
            disscussPosts.add(map);
        }
//        map.put("page", page);
        model.addAttribute("disscussPosts", disscussPosts);

        return "/index";
    }

    /**
     * 权限不足拦截
     * @return
     */
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }

}
