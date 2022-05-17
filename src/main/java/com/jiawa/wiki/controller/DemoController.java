package com.jiawa.wiki.controller;

import com.jiawa.wiki.domain.Demo;
import com.jiawa.wiki.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:16 PM
 * @Describe:
 */
@RestController
@RequestMapping("demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/hello")
    public List<Demo> getAll() {
        return demoService.getAll();
    }
}
