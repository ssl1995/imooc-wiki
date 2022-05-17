package com.jiawa.wiki.service.impl;

import com.jiawa.wiki.domain.Test;
import com.jiawa.wiki.mapper.TestMapper;
import com.jiawa.wiki.service.TestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 9:14 PM
 * @Describe:
 */
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private TestMapper testMapper;

    @Override
    public List<Test> getAll() {
        return testMapper.getAll();
    }
}
