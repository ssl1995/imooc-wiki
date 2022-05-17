package com.jiawa.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/hello")
    public String helloTest() {
        return "hello:" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPostTest(String name) {
        return "hello post " + name;
    }
}
