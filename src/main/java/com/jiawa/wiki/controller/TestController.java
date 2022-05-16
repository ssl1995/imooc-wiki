package com.jiawa.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/16 11:26 PM
 * @Describe:
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public String helloTest() {
        return "hello";
    }
}
