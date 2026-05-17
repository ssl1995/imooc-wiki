package com.jiawa.wiki.mapper;

import com.jiawa.wiki.domain.Tree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TreeMapper {

    List<Tree> selectAll();

    List<Tree> selectByName(@Param("name") String name);

    Tree selectById(@Param("id") Long id);

    Tree selectByTreeCode(@Param("treeCode") String treeCode);

    int insert(Tree record);
}
