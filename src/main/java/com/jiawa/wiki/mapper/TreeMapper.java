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

    /**
     * 根据科属种条件筛选古树
     */
    List<Tree> selectByTaxonomy(@Param("family") String family,
                                @Param("genus") String genus,
                                @Param("species") String species);

    /**
     * 查询所有科（去重）
     */
    List<String> selectAllFamilies();

    /**
     * 根据科查询下属所有属（去重）
     */
    List<String> selectGeneraByFamily(@Param("family") String family);

    /**
     * 根据科和属查询下属所有种（去重）
     */
    List<String> selectSpeciesByGenus(@Param("family") String family,
                                      @Param("genus") String genus);

    /**
     * 综合条件筛选：支持保护级别、树龄范围等
     */
    List<Tree> selectByFilter(@Param("family") String family,
                              @Param("genus") String genus,
                              @Param("species") String species,
                              @Param("protectionLevel") String protectionLevel,
                              @Param("minAge") Integer minAge,
                              @Param("maxAge") Integer maxAge);

    int insert(Tree record);
}
