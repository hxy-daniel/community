package com.cqupt.community.controller.interceptor;

import com.cqupt.community.entity.LoginTicket;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.CookieUtil;
import com.cqupt.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.omg.IOP.ServiceContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if (!StringUtils.isBlank(ticket)) {
            LoginTicket loginTicket = userService.selectLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                User user = userService.getUserById(loginTicket.getUserId());
                if (user != null) {
                    // 将用户存入ThreadLocal中，防止并发冲突问题
                    hostHolder.setUser(user);

                    // 将token加入SecurityContext中
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user, user.getPassword(), userService.getAuthorities(user.getId()));
                    SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
//                  SecurityContextHolder.setContext(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(
//                          user, user.getPassword(), userService.getAuthorities(user.getId())
//                  )));
                }


            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        // 这里不能清空，否则第二次点击需要登录的链接会跳转到登录
//        SecurityContextHolder.clearContext();
    }
}
