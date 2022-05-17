package com.jiawa.wiki.mapper;

import com.jiawa.wiki.domain.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 9:11 PM
 * @Describe:
 */
@Mapper
public interface TestMapper {

    List<Test> getAll();
}
