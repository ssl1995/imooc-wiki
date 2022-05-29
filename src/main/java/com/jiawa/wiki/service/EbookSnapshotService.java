package com.jiawa.wiki.service;

import com.jiawa.wiki.resp.StatisticResp;

import java.util.List;

/**
 * @author SongShengLin
 * @date 2022/5/29 10:19
 * @description
 */
public interface EbookSnapshotService {

    void getSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
