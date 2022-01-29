package com.cqupt.community.controller;

import com.cqupt.community.entity.DiscussPost;
import com.cqupt.community.entity.Page;
import com.cqupt.community.service.ElasticsearchService;
import com.cqupt.community.service.LikeService;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService searchService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException, ParseException {
        page.setPageSize(5);
        // 搜索帖子
        List<DiscussPost> searchResult =
                searchService.searchDiscussPost(keyword, page.getOffSet(), page.getPageSize());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.getUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.likeCount(ENTITY_TYPE_DISCUSSPOST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setTotals(searchResult == null ? 0 : searchResult.size());

        return "/site/search";
    }
}
