package com.jiawa.wiki.mapper;

import com.jiawa.wiki.domain.ImageInfo;
import java.util.List;

public interface ImageInfoMapper {
    List<ImageInfo> selectAll();
    ImageInfo selectById(Long id);
    ImageInfo selectByTreeId(Long treeId);
}
