package edu.cgdi.labeler.model;

public class ExportAttributeRow {
    private Long annotationId;
    private String dimensionCode;
    private String attributeCode;
    private Boolean present;
    private String valueText;

    public Long getAnnotationId() { return annotationId; }
    public void setAnnotationId(Long annotationId) { this.annotationId = annotationId; }
    public String getDimensionCode() { return dimensionCode; }
    public void setDimensionCode(String dimensionCode) { this.dimensionCode = dimensionCode; }
    public String getAttributeCode() { return attributeCode; }
    public void setAttributeCode(String attributeCode) { this.attributeCode = attributeCode; }
    public Boolean getPresent() { return present; }
    public void setPresent(Boolean present) { this.present = present; }
    public String getValueText() { return valueText; }
    public void setValueText(String valueText) { this.valueText = valueText; }
}
