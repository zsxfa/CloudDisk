package com.zsxfa.cloud.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zsxfa
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTemplateTests {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void saveDict(){
        redisTemplate.opsForValue().set("apple2", "test", 5, TimeUnit.MINUTES);

    }
    @Test
    public void getDict(){
        Object dict = redisTemplate.opsForValue().get("dict");
        System.out.println(dict);
    }
}
