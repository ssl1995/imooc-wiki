package com.jiawa.wiki.service.impl;

import com.jiawa.wiki.mapper.EbookSnapshotCustMapper;
import com.jiawa.wiki.resp.StatisticResp;
import com.jiawa.wiki.service.EbookSnapshotService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public List<StatisticResp> getStatistic() {
        return ebookSnapshotCustMapper.getStatistic();
    }

    @Override
    public List<StatisticResp> get30Statistic() {
        return ebookSnapshotCustMapper.get30Statistic();
    }
}
