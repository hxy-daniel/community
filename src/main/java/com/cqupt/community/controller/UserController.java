package com.cqupt.community.controller;

import com.cqupt.community.annotation.LoginRequired;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.FollowService;
import com.cqupt.community.service.LikeService;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.CommunityConstant;
import com.cqupt.community.util.CommunityUtil;
import com.cqupt.community.util.CookieUtil;
import com.cqupt.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String setting() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String upload(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "请选择上传的图片！");
            return "/site/setting";
        }

        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "图片格式不正确！");
            return "/site/setting";
        }
        String newName = CommunityUtil.generateUUID() + suffix;
        File file = new File(uploadPath + newName);

        try {
            headerImage.transferTo(file);
            String headerUrl = domain + contextPath + "/user/header/" + newName;
            userService.updateUserHeaderUrl(hostHolder.getUser().getId(), headerUrl);

            // TODO:这里为什么需要重定向
            return "redirect:/index";
        } catch (IOException e) {
            model.addAttribute("error", "文件上传出错！");
            logger.error("文件上传失败！"+ e);
            return "/site/setting";
        }
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void headerUrl(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        String filePath = uploadPath + fileName;

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/suffix");
        try (
                FileInputStream fileInputStream = new FileInputStream(filePath);
                OutputStream outputStream = response.getOutputStream();
                ){
            byte[] buffer = new byte[1024];
            int b = 0;

            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer,0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败！" + e);
        }
    }

    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(HttpServletRequest request, String originPassword, String newPassword, Model model) {
        String ticket = CookieUtil.getValue(request, "ticket");
        Map<String, Object> map = userService.updatePassword(ticket, originPassword, newPassword);
        if (map.containsKey("originPasswordError")) {
            model.addAttribute("originPasswordMsg", map.get("originPasswordError"));
            return "/site/setting";
        }

        if (map.containsKey("newPasswordError")) {
            model.addAttribute("newPasswordMsg", map.get("newPasswordError"));
            return "/site/setting";
        }
        return "/site/login";
    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String profile(@PathVariable("userId") int userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        int userLikeCount = likeService.getUserLikeCount(userId);
        model.addAttribute("likeCount", userLikeCount);
        boolean followed = false;
        if (hostHolder.getUser() != null){
            followed = followService.followed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("followed", followed);
        model.addAttribute("followeeCount", followService.followeeCount(userId, ENTITY_TYPE_USER));
        model.addAttribute("followerCount", followService.followerCount(ENTITY_TYPE_USER, userId));
        return "/site/profile";
    }
}
