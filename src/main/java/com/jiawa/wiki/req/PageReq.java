package com.jiawa.wiki.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/22 8:51 AM
 * @Describe:
 */
public class PageReq {
    @NotNull(message = "【页面】不能为空")
    private Integer page;

    @NotNull(message = "【分页】不能为空")
    @Max(value = 1000, message = "【分页】最大不超过1000条")
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
