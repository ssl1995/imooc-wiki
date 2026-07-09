package com.jiawa.wiki.req;

public class TreeRetrieveReq {

  /**
   * 检索类型：I2I / I2L / L3I / NAME
   */
  private TreeRetrieveEnum type;

  /**
   * 树种名称（NAME检索用）
   */
  private String speciesName;
  /**
   * 最小树龄（NAME检索用）
   */
  private Integer minAge;

  /**
   * 最大树龄（NAME检索用）
   */
  private Integer maxAge;


  /**
   * 纬度（L3I检索用）
   */
  private Double latitude;

  /**
   * 经度（L3I检索用）
   */
  private Double longitude;

  /**
   * 搜索半径，单位km（L3I检索用）
   */
  private Double radius = 5.0;

  /**
   * 返回结果数上限
   */
  private Integer topK = 6;

  public TreeRetrieveEnum getType() {
    return type;
  }

  public void setType(TreeRetrieveEnum type) {
    this.type = type;
  }

  public String getSpeciesName() {
    return speciesName;
  }

  public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getRadius() {
    return radius;
  }

  public void setRadius(Double radius) {
    this.radius = radius;
  }

  public Integer getTopK() {
    return topK;
  }

  public void setTopK(Integer topK) {
    this.topK = topK;
  }

  public Integer getMinAge() {
    return minAge;
  }

  public void setMinAge(Integer minAge) {
    this.minAge = minAge;
  }

  public Integer getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(Integer maxAge) {
    this.maxAge = maxAge;
  }
}
