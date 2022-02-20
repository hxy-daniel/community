package com.cqupt.community;

import com.cqupt.community.dao.DiscussPostDao;
import com.cqupt.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPostTest {
    @Autowired
    DiscussPostDao discussPostDao;
    @Test
    public void getNewestDiscussPostsTest() {
        List<DiscussPost> discussPosts = discussPostDao.getDiscussPosts(0, 0, 10, 0);
        for (DiscussPost post : discussPosts) {
            System.out.println(post);
        }
    }

    @Test
    public void testCache() {
        System.out.println(discussPostDao.getDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostDao.getDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostDao.getDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostDao.getDiscussPosts(0, 0, 10, 0));
    }
}
