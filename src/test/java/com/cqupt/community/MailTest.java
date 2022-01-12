package com.cqupt.community;

import com.cqupt.community.util.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    MailUtil mailUtil;

    @Test
    public void sendMailTest() {
        mailUtil.sendMail("1262912010@qq.com","xixi","熊猫张张你好！");
    }
}
