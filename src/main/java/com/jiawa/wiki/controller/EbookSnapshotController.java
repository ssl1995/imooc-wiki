package com.jiawa.wiki.controller;

import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.resp.StatisticResp;
import com.jiawa.wiki.service.EbookSnapshotService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/ebook-snapshot")
public class EbookSnapshotController {

    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @GetMapping("/get-statistic")
    public CommonResp getStatistic() {
        // 集合:[昨天，今天]
        List<StatisticResp> statisticResp = ebookSnapshotService.getStatistic();
        // 服务器项目初始化时候，如果昨天没有数据，返回的是只有[今天]，前端写死了这段逻辑，后端改比较好改
        checkStatisticResp(statisticResp);

        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        return commonResp;
    }

    private void checkStatisticResp(List<StatisticResp> statisticResp) {
        StatisticResp beforeResp = new StatisticResp();
        StatisticResp todayResp = new StatisticResp();

        Date todayDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date beforeDate = calendar.getTime();

        if (CollectionUtils.isEmpty(statisticResp)) {
            beforeResp.setDate(beforeDate);
            beforeResp.setViewCount(0);
            beforeResp.setVoteCount(0);
            beforeResp.setViewIncrease(0);
            beforeResp.setVoteIncrease(0);

            todayResp.setDate(todayDate);
            todayResp.setViewCount(0);
            todayResp.setVoteCount(0);
            todayResp.setViewIncrease(0);
            todayResp.setVoteIncrease(0);

            statisticResp.add(beforeResp);
            statisticResp.add(todayResp);

        } else if (Objects.equals(statisticResp.size(), 1)) {
            beforeResp.setDate(beforeDate);
            beforeResp.setViewCount(0);
            beforeResp.setVoteCount(0);
            beforeResp.setViewIncrease(0);
            beforeResp.setVoteIncrease(0);

            statisticResp.add(beforeResp);
            // 交换集合中的元素位置，保持[昨天，今天]
            Collections.swap(statisticResp, 0, 1);

        }

    }

    @GetMapping("/get-30-statistic")
    public CommonResp get30Statistic() {
        List<StatisticResp> statisticResp = ebookSnapshotService.get30Statistic();
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        return commonResp;
    }

}
