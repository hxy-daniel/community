package com.cqupt.community.service;

import com.cqupt.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
    public void like(int userId, int entityType, int entityId, int targetUserId) {
//        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        Boolean isLiked = redisTemplate.opsForSet().isMember(key, userId);
//        if (isLiked) {
//            redisTemplate.opsForSet().remove(key, userId);
//        } else {
//            redisTemplate.opsForSet().add(key, userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(targetUserId);
                Boolean isLiked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();
                if (isLiked) {
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                } else {
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });

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

    /**
     * 获取用户收到的赞
     * @param userId
     * @return
     */
    public int getUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer userLikeCount = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return userLikeCount == null ? 0 : userLikeCount.intValue();
    }
}
