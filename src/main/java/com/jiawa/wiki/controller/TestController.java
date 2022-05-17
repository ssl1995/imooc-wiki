package com.jiawa.wiki.controller;

import com.jiawa.wiki.domain.Test;
import com.jiawa.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/16 11:26 PM
 * @Describe:
 */
@RestController
public class TestController {

    /**
     * 自定义配置项，：配置默认值
     */
    @Value("${test.hello:Test}")
    private String testHello;

    @Resource
    private TestService testService;

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
}
