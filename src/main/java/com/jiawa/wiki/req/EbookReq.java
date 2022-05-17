package com.jiawa.wiki.req;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 11:05 PM
 * @Describe:
 */
public class EbookReq {
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EbookReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
