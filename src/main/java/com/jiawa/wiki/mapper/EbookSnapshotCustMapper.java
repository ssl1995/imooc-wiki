package com.jiawa.wiki.mapper;

import com.jiawa.wiki.resp.StatisticResp;

import java.util.List;

/**
 * @author SongShengLin
 * @date 2022/5/29 10:12
 * @description
 */
public interface EbookSnapshotCustMapper {

    void getSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
