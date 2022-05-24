package com.jiawa.wiki.req;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/22 9:08 PM
 * @Describe:
 */
public class DocQueryReq extends PageReq {

    private Long id;

    private String ebookId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEbookId() {
        return ebookId;
    }

    public void setEbookId(String ebookId) {
        this.ebookId = ebookId;
    }

    @Override
    public String toString() {
        return "DocQueryReq{" +
                "id=" + id +
                ", ebookId='" + ebookId + '\'' +
                "} " + super.toString();
    }
}
