package com.jiawa.wiki.service.impl;

import com.jiawa.wiki.domain.Demo;
import com.jiawa.wiki.mapper.DemoMapper;
import com.jiawa.wiki.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:13 PM
 * @Describe:
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoMapper demoMapper;


    @Override
    public List<Demo> getAll() {
        return demoMapper.selectByExample(null);
    }
}
