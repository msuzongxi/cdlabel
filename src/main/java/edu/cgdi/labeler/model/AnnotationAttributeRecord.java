package edu.cgdi.labeler.model;

public class AnnotationAttributeRecord {
    private Long annotationDimensionId;
    private String attributeCode;
    private boolean present;
    private String valueText;

    public Long getAnnotationDimensionId() { return annotationDimensionId; }
    public void setAnnotationDimensionId(Long annotationDimensionId) { this.annotationDimensionId = annotationDimensionId; }
    public String getAttributeCode() { return attributeCode; }
    public void setAttributeCode(String attributeCode) { this.attributeCode = attributeCode; }
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
    public String getValueText() { return valueText; }
    public void setValueText(String valueText) { this.valueText = valueText; }
}
