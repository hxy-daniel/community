package com.cqupt.community.controller.intercepter;

import com.cqupt.community.service.UserService;
import com.cqupt.community.util.CommunityUtil;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping(path = "/user")
public class UserController {

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

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String setting() {
        return "/site/setting";
    }

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
}
