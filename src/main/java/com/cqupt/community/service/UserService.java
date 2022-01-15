package com.cqupt.community.service;

import com.cqupt.community.dao.LoginTicketDao;
import com.cqupt.community.dao.UserDao;
import com.cqupt.community.entity.LoginTicket;
import com.cqupt.community.entity.User;
import com.cqupt.community.util.*;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserDao userDao;

    @Autowired
    private MailUtil mailUtil;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Autowired
    private HostHolder hostHolder;

    // 根据id查询用户
    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    // 注册用户
    public Map<String, Object> registerUser(User user) {
        Map res = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("用户参数不能为空！");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            res.put("usernameMsg", "用户名不能为空！");
            return res;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            res.put("passwordMsg", "密码不能为空！");
            return res;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            res.put("emailMsg", "邮箱不能为空！");
            return res;
        }

        if (StringUtils.isBlank(user.getUsername())) {
            res.put("usernameMsg", "用户名不能为空！");
            return res;
        }

        User u = userDao.selectByName(user.getUsername());
        if (u != null) {
            res.put("usernameMsg", "该用户名已存在");
            return res;
        }

        u = userDao.selectByEmail(user.getEmail());
        if (u != null) {
            res.put("emailMsg", "该邮箱已注册！");
            return res;
        }

        if (user.getEmail().equals("") || user.getEmail() == null) {
            res.put("emailMsg", "请输入邮箱！");
            return res;
        }

        user.setActivationCode(CommunityUtil.generateUUID());
        user.setCreateTime(new Date());
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setStatus(0);
        user.setType(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        int i = userDao.insertUser(user);
        // 邮件处理
        Context context = new Context();
        Map var = new HashMap<>();
        var.put("email", user.getEmail());

        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        var.put("url", url);
        context.setVariables(var);

        String content = templateEngine.process("/mail/activation", context);
        mailUtil.sendMail(user.getEmail(),"账户激活",content);

        return res;
    }

    public int activation(int userId, String code) {
        User user = userDao.getUserById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userDao.updateUserStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int seconds) {
        User user = userDao.selectByName(username);
        Map<String, Object> res = new HashMap<>();

        if (StringUtils.isBlank(username)) {
            res.put("usernameMsg", "账号不能为空！");
            return res;
        }

        if (StringUtils.isBlank(password)) {
            res.put("passwordMsg", "密码不能为空！");
            return res;
        }

        if (user == null) {
            res.put("usernameMsg", "该用户不存在！");
            return res;
        }

        if (user.getStatus() == 0) {
            res.put("usernameMsg", "该用户未激活！");
            return res;
        }

        if (!user.getPassword().equals(CommunityUtil.md5(password + user.getSalt()))) {
            res.put("passwordMsg", "密码错误！");
            return res;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        String ticket = CommunityUtil.generateUUID();
        loginTicket.setTicket(ticket);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * seconds));
        loginTicketDao.insertTicket(loginTicket);

        res.put("ticket", ticket);
        return res;
    }

    public LoginTicket selectLoginTicket(String ticket) {
        return loginTicketDao.selectByTicket(ticket);
    }

    public int updateUserHeaderUrl(int userId, String headerUrl){
        return userDao.updateUserHeaderUrl(userId, headerUrl);
    }

    public Map<String, Object> updatePassword(String ticket, String originPassword, String newPassword) {

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(originPassword)) {
            map.put("originPasswordError", "请输入原始密码！");
            return map;
        }

        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordError", "请输入新的密码！");
            return map;
        }

        User user = hostHolder.getUser();
        String mdPass = CommunityUtil.md5(originPassword);
        if (!CommunityUtil.md5(originPassword + user.getSalt()).equals(user.getPassword())) {
            map.put("originPasswordError", "原始密码错误！");
            return map;
        }

        int userId = user.getId();
        userDao.updateUserPassword(userId, CommunityUtil.md5(newPassword  + user.getSalt()));
        loginTicketDao.updateStatus(ticket, 1);
        return map;
    }
}
