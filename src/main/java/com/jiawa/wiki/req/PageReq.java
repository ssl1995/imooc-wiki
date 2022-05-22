package com.jiawa.wiki.req;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/22 8:51 AM
 * @Describe:
 */
public class PageReq {

    private Integer num;

    private Integer size;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
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
                "num=" + num +
                ", size=" + size +
                '}';
    }
}
