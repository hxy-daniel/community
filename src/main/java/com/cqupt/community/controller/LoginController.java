package com.cqupt.community.controller;

import com.cqupt.community.dao.LoginTicketDao;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.CommunityConstant;
import com.cqupt.community.util.CommunityUtil;
import com.cqupt.community.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import com.jhlabs.image.ImageUtils;
import com.sun.imageio.plugins.common.ImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer captchaProducer;

    @Value("${server.servlet.context-path}")
    private String contexPath;

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String register(){
        return "/site/register";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.registerUser(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，请尽快通过邮件激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") String userId, @PathVariable("code") String code) {
        int result = userService.activation(Integer.parseInt(userId), code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login(){
        return "/site/login";
    }

//    @RequestMapping(path = "/login", method = RequestMethod.POST)
//    public String login(Model model, User user){
//        User u = userService.getUserById(user.getId());
//        if (u.getPassword().equals(CommunityUtil.md5(user.getPassword()))) {
//            return "/index";
//        }
//        return null;
//    }

//    @RequestMapping(path = "/captcha", method = RequestMethod.GET)
//    public void captcha(HttpServletResponse response, HttpSession session) {
//        String text = captchaProducer.createText();
//        session.setAttribute("captcha", text);
//        BufferedImage image = captchaProducer.createImage(text);
//        try {
////            PrintWriter writer = response.getWriter();
//            ServletOutputStream outputStream = response.getOutputStream();
//            ImageIO.write(image, "png", outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @RequestMapping(path = "/captcha", method = RequestMethod.GET)
    public void captcha(HttpServletResponse response) {
        String text = captchaProducer.createText();
        String owner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("owner", owner);
        cookie.setMaxAge(60);
        cookie.setPath(contexPath);
        response.addCookie(cookie);
        String captchaKey = RedisKeyUtil.getCaptchaKey(owner);
        redisTemplate.opsForValue().set(captchaKey, text, 60, TimeUnit.SECONDS);
//        session.setAttribute("captcha", text);
        BufferedImage image = captchaProducer.createImage(text);
        try {
//            PrintWriter writer = response.getWriter();
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @RequestMapping(path = "/login", method = RequestMethod.POST)
//    public String login(String username, String password, String code, boolean rememberme,
//                        HttpServletResponse response, Model model, HttpSession session) {
//        String captcha = (String) session.getAttribute("captcha");
//        if (StringUtils.isBlank(code) || StringUtils.isBlank(captcha) || !code.equalsIgnoreCase(captcha)) {
//            model.addAttribute("codeMsg","验证码错误！");
//            return "/site/login";
//        }
//        int seconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
//        Map<String, Object> map = userService.login(username, password, seconds);
//
//        if (map.containsKey("ticket")) {
//            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
//            cookie.setPath(contexPath);
//            cookie.setMaxAge(seconds);
//            response.addCookie(cookie);
//            return "redirect:/index";
//        } else {
//            model.addAttribute("usernameMsg", map.get("usernameMsg"));
//            model.addAttribute("passwordMsg", map.get("passwordMsg"));
//            return "/site/login";
//        }
//    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        HttpServletResponse response, Model model, @CookieValue("owner") String owner) {
        String captcha = null;
        if (!StringUtils.isBlank(owner)) {
            String captchaKey = RedisKeyUtil.getCaptchaKey(owner);
            captcha = (String) redisTemplate.opsForValue().get(captchaKey);
        }
        if (StringUtils.isBlank(code) || StringUtils.isBlank(captcha) || !code.equalsIgnoreCase(captcha)) {
            model.addAttribute("codeMsg","验证码错误！");
            return "/site/login";
        }
        int seconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, seconds);

        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setPath(contexPath);
            cookie.setMaxAge(seconds * 1000);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
//        loginTicketDao.updateStatus(ticket, 1);
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
