package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void initDateForTest() {
        for (int i = 0; i < 200000; i++){
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("压力测试数据");
            post.setContent("压力测试的数据内容");
            post.setCreateTime(new Date());
            post.setScore(Math.random()*2000);
            discussPostService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache() {
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,0));

    }
}
