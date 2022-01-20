package com.cqupt.community.controller;

import com.cqupt.community.entity.Message;
import com.cqupt.community.entity.Page;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.MessageService;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.HostHolder;
import com.cqupt.community.util.JsonResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

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
     *
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
        List<Map<String, Object>> messageList = new ArrayList<>();
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

    /**
     * 会话详细页
     *
     * @param conversationId
     * @return
     */
    @RequestMapping(path = "/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setPageSize(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setTotals(messageService.selectLetterCount(conversationId));
        List<Message> messages = messageService.selectConversationDetail(conversationId, page.getOffSet(), page.getPageSize());
        List<Map<String, Object>> letterList = new ArrayList<>();
        for (Message m : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("letter", m);
            User targetUser = userService.getUserById(m.getFromId());
            map.put("targetUser", targetUser);
            letterList.add(map);
        }
        User targetUser = getLetterTarget(conversationId);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("letterList", letterList);
        List<Integer> ids = getLetterIds(messages);
        // 读消息
        if (!ids.isEmpty()) {
            messageService.readLetter(ids);
        }
        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.getUserById(id1);
        } else {
            return userService.getUserById(id0);
        }
    }

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User toUser = userService.selectByName(toName);
        if (toUser == null) {
            return JsonResponseUtils.toJsonResponse(500, "目标用户不存在!");
        }
        User fromUser = hostHolder.getUser();
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        String conversationId = Math.min(fromUser.getId(), toUser.getId()) + "_" + Math.max(fromUser.getId(), toUser.getId());
        message.setConversationId(conversationId);
        messageService.insertLetter(message);
        return JsonResponseUtils.toJsonResponse(200, "发送成功");
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }
}
