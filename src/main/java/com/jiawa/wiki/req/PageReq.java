package com.jiawa.wiki.req;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/22 8:51 AM
 * @Describe:
 */
public class PageReq {

    private Integer page;

    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageReq{" +
                "num=" + page +
                ", size=" + size +
                '}';
    }
}
