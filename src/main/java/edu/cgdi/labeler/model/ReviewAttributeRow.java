package edu.cgdi.labeler.model;

public class ReviewAttributeRow {
    private Long annotationId;
    private String dimensionCode;
    private String attributeCode;

    public Long getAnnotationId() { return annotationId; }
    public void setAnnotationId(Long annotationId) { this.annotationId = annotationId; }
    public String getDimensionCode() { return dimensionCode; }
    public void setDimensionCode(String dimensionCode) { this.dimensionCode = dimensionCode; }
    public String getAttributeCode() { return attributeCode; }
    public void setAttributeCode(String attributeCode) { this.attributeCode = attributeCode; }
}
