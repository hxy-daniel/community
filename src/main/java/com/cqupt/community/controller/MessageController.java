package com.cqupt.community.controller;

import com.cqupt.community.entity.Message;
import com.cqupt.community.entity.Page;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.MessageService;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/letter")
public class MessageController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    /**
     * 查询会话
     * @return
     */
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public String selectLetter(Model model, Page page) {
        page.setPageSize(5);
        page.setPath("/letter/list");
        int userId = hostHolder.getUser().getId();
        page.setTotals(messageService.selectConversationCount(userId));
        // TODO:分页最后一页空白bug

        List<Message> messages = messageService.selectConversations(userId, page.getOffSet(), page.getPageSize());
        model.addAttribute("unReadCount", messageService.selectUnReadCount(userId, null));
        List<Map<String,Object>> messageList = new ArrayList<>();
        for (Message m : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("conversation", m);
            map.put("letterCount", messageService.selectLetterCount(m.getConversationId()));
            map.put("unReadCount", messageService.selectUnReadCount(userId, m.getConversationId()));
            int targetUserId = hostHolder.getUser().getId() == m.getFromId() ? m.getToId() : m.getFromId();
            User targetUser = userService.getUserById(targetUserId);
            map.put("targetUser", targetUser);
            messageList.add(map);
        }

        model.addAttribute("conversations", messageList);
        return "/site/letter";
    }

    @RequestMapping(path = "/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId) {


        return "/site/letter-detail";
    }
}
