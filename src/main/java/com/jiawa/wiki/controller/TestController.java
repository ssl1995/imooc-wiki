package com.jiawa.wiki.controller;

import com.jiawa.wiki.domain.Test;
import com.jiawa.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/16 11:26 PM
 * @Describe:
 */
@RestController
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);


    /**
     * 自定义配置项，：配置默认值
     */
    @Value("${test.hello:Test}")
    private String testHello;

    @Resource
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/hello")
    public String helloTest() {
        return "hello:" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPostTest(String name) {
        return "hello post " + name;
    }

    @GetMapping("/test/hello")
    public List<Test> getAll() {
        return testService.getAll();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
}
