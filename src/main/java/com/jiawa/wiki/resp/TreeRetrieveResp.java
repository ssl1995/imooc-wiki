package com.jiawa.wiki.resp;

import java.math.BigDecimal;

public class TreeRetrieveResp {

    private Long id;
    private String treeCode;
    private String name;
    private String species;
    private String family;
    private String genus;
    private String protectionLevel;
    private Integer age;
    private String height;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String location;
    private String hashCode;
    private String image;
    private Double similarity;
    private Double distance;
    private Double confidence;
    private Double error;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(String protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Double getError() {
        return error;
    }

    public void setError(Double error) {
        this.error = error;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", treeCode=").append(treeCode);
        sb.append(", name=").append(name);
        sb.append(", species=").append(species);
        sb.append(", family=").append(family);
        sb.append(", genus=").append(genus);
        sb.append(", protectionLevel=").append(protectionLevel);
        sb.append(", age=").append(age);
        sb.append(", height=").append(height);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", location=").append(location);
        sb.append(", hashCode=").append(hashCode);
        sb.append(", image=").append(image);
        sb.append(", similarity=").append(similarity);
        sb.append(", distance=").append(distance);
        sb.append(", confidence=").append(confidence);
        sb.append(", error=").append(error);
        sb.append("]");
        return sb.toString();
    }
}
