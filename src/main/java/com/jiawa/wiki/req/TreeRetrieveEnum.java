package com.jiawa.wiki.req;

public enum TreeRetrieveEnum {

  I2I( "图像到图像检索"),
  I2L( "图像到位置检索"),
  L3I( "位置到图像检索"),
  NAME("树种名称检索")
  ;
  private final String desc;

  TreeRetrieveEnum(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }
}
