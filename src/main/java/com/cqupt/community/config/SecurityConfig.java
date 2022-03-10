package com.cqupt.community.config;

import com.cqupt.community.entity.LoginTicket;
import com.cqupt.community.entity.User;
import com.cqupt.community.service.UserService;
import com.cqupt.community.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    /**
     * 使用已有的认证，认证后将token加入SecurityContext中即可
     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//    }

    /**
     * 授权处理
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                HttpServletResponse response = (HttpServletResponse) servletResponse;
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
                        }
                    }
                }
                // 让请求继续向下执行.
                filterChain.doFilter(request, response);
            }
        }, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests().antMatchers(
                "/user/setting",
                "/user/upload",
                "/discussPost/add",
                "/comment/add/**",
                "/letter/**",
                "/notice/**",
                "/like",
                "/follow",
                "/unfollow"
        ).hasAnyAuthority(
                AUTHORITY_USER,
                AUTHORITY_ADMIN,
                AUTHORITY_MODERATOR
        ).antMatchers(
                "/discussPost/top",
                "/discussPost/wonderful"
        )
        .hasAnyAuthority(AUTHORITY_MODERATOR)
        .antMatchers(
                "/discussPost/delete",
                "/data/**"
        )
        .hasAnyAuthority(AUTHORITY_ADMIN)
        .anyRequest().permitAll()
        .and().csrf().disable();

        // 处理未登录的情况
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                String xRequestedWith = request.getHeader("x-requested-with");
                if ("XMLHttpRequest".equals(xRequestedWith)) {
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(JsonResponseUtils.toJsonResponse(403, "你还没有登录哦!"));
                } else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }
            }
        })
            // 处理权限不足的情况
            .accessDeniedHandler(new AccessDeniedHandler() {
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(JsonResponseUtils.toJsonResponse(403, "你没有访问此功能的权限!"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/denied");
                    }
                }
            });

        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行已有的退出代码.
        http.logout().logoutUrl("/securitylogout");
    }
}
