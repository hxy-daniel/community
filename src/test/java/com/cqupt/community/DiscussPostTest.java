package com.cqupt.community;

import com.cqupt.community.dao.DiscussPostDao;
import com.cqupt.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPostTest {
    @Autowired
    DiscussPostDao discussPostDao;
    @Test
    public void getNewestDiscussPostsTest() {
        List<DiscussPost> discussPosts = discussPostDao.getDiscussPosts(0, 0, 10);
        for (DiscussPost post : discussPosts) {
            System.out.println(post);
        }
    }
}
