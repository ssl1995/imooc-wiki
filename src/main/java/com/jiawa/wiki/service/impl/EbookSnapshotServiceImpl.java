package com.jiawa.wiki.service.impl;

import com.jiawa.wiki.mapper.EbookSnapshotCustMapper;
import com.jiawa.wiki.service.EbookSnapshotService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author SongShengLin
 * @date 2022/5/29 10:19
 * @description
 */
@Service
public class EbookSnapshotServiceImpl implements EbookSnapshotService {

    @Resource
    private EbookSnapshotCustMapper ebookSnapshotCustMapper;

    @Override
    public void getSnapshot() {
        ebookSnapshotCustMapper.getSnapshot();
    }
}
