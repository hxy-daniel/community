package com.cqupt.community.service;

import com.cqupt.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void like(int userId, int entityType, int entityId) {
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isLiked = redisTemplate.opsForSet().isMember(key, userId);
        if (isLiked) {
            redisTemplate.opsForSet().remove(key, userId);
        } else {
            redisTemplate.opsForSet().add(key, userId);
        }
    }

    /**
     * 点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long likeCount(int entityType, int entityId) {
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 用户是否点赞
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int isLiked(int userId, int entityType, int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(key, userId) ? 1 : 0;
    }
}
