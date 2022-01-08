package com.cqupt.community.controller;

import com.cqupt.community.entity.DiscussPost;
import com.cqupt.community.entity.Page;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.DiscussPostService;
import com.cqupt.community.service.UserService;
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
public class IndexController {

    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String showIndex(Model model, Page page) {
        page.setPath("/index");
        page.setTotals(discussPostService.getTotals(0));
        List<DiscussPost> list = discussPostService.getDiscussPosts(0, page.getOffSet(), page.getPageSize());
        List<Map<String,Object>> disscussPosts = new ArrayList<>();
        for (DiscussPost post : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            User user = userService.getUserById(Integer.parseInt(post.getUserId()));
            map.put("user", user);
            disscussPosts.add(map);
        }
//        map.put("page", page);
        model.addAttribute("disscussPosts", disscussPosts);

        return "/index";
    }

}
