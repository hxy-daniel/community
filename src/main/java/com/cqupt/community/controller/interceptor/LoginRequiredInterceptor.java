package com.cqupt.community.controller.interceptor;

import com.cqupt.community.annotation.LoginRequired;
import com.cqupt.community.util.HostHolder;
import com.cqupt.community.util.JsonResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if (loginRequired != null && hostHolder.getUser() == null) {
                String xRequestWith = request.getHeader("x-requested-with");
                // TODO:异步要求登录待处理 如：未登录点赞操作报错
                if ("XmlHttpRequest".equals(xRequestWith)) {
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(JsonResponseUtils.toJsonResponse(500, "请登录后再操作！"));
                } else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }

                return false;
            }
        }

        return true;
    }
}
