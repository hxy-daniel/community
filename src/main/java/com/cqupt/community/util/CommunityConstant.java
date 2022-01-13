package com.cqupt.community.util;

public interface CommunityConstant {
    // 激活成功
    int ACTIVATION_SUCCESS=0;

    // 重复激活
    int ACTIVATION_REPEAT=1;

    // 激活失败
    int ACTIVATION_FAILURE=2;

    /**
     * 登录默认过期时间
     */
    int DEFAULT_EXPIRED_SECONDS = 60 * 60 * 12;

    /**
     * 长期登录时间
     */
    int REMEMBER_EXPIRED_SECONDS = 60 * 60 * 24 * 7;
}
