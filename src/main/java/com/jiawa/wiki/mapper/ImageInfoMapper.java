package com.jiawa.wiki.mapper;

import com.jiawa.wiki.domain.Image;
import java.util.List;

public interface ImageInfoMapper {
    List<Image> selectAll();
    Image selectById(Long id);
    Image selectByTreeId(Long treeId);
    int insert(Image image);
}
