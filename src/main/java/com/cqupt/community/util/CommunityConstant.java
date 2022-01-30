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

    /**
     * 实体类型：帖子
     * 属于帖子的评论
     */
    int ENTITY_TYPE_DISCUSSPOST = 1;

    /**
     * 实体类型：评论
     * 属于回复的评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题: 评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题: 点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题: 关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 主题：删帖
     */
    String TOPIC_DELETE = "delete";
    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 管理员权限
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 版主权限
     */
    String AUTHORITY_MODERATOR = "moderator";

    /**
     * 普通用户权限
     */
    String AUTHORITY_USER = "user";
}
