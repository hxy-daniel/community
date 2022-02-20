package com.cqupt.community.service;

import com.cqupt.community.dao.DiscussPostDao;
import com.cqupt.community.entity.DiscussPost;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostDao discussPostDao;

    @Value("${caffeine.posts.max-size}")
    private int caffeineMaxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int caffeineExpireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;


    /**
     * Service启动时初始化本地缓存
     */
    @PostConstruct
    public void init() {
        // 初始化热门帖子缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(caffeineMaxSize)
                .expireAfterWrite(caffeineExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // 二级缓存: Redis -> mysql

                        logger.debug("load post list from DB.");
//                        System.out.println("load post list from DB.");
                        return discussPostDao.getDiscussPosts(0, offset, limit, 1);
                    }
        });

        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(caffeineMaxSize)
                .expireAfterWrite(caffeineExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer userId) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostDao.getTotals(userId);
                    }
                });
    }

    public List<DiscussPost> getDiscussPosts(int userId, int offset, int limit, int orderMode){
        if (userId == 0 && orderMode == 1) {
            logger.debug("load post list from caffeine.");
//            System.out.println("load post list from caffeine.");
            return postListCache.get(offset + ":" + limit);
        }
//        System.out.println("load post list from DB.");
        logger.debug("load post list from DB.");
        return discussPostDao.getDiscussPosts(userId, offset, limit, orderMode);
    }

    public int getTotals(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        logger.debug("load post rows from DB.");
        return discussPostDao.getTotals(userId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        return discussPostDao.addDiscussPost(discussPost);
    }

    public DiscussPost selectDiscussPostById(int id) {
        return discussPostDao.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostDao.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostDao.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostDao.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostDao.updateScore(id, score);
    }
}
