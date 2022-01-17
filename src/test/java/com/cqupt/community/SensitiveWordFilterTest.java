package com.cqupt.community;

import com.cqupt.community.util.SensitiveWordFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveWordFilterTest {

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Test
    public void sensitiveWordFilterTest() {
        String text = "很多人喜欢☆赌☆博☆和吸☆毒，这是不可取的！";
        text = sensitiveWordFilter.filter(text);
        System.out.println(text);

    }
}
