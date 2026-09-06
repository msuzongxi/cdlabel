package edu.cgdi.labeler.model;

public class AttributeDef {
    private String code;
    private String dimensionCode;
    private String label;
    private String meaning;
    private String positiveExample;
    private String negativeExample;
    private Integer displayOrder;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDimensionCode() { return dimensionCode; }
    public void setDimensionCode(String dimensionCode) { this.dimensionCode = dimensionCode; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }

    public String getPositiveExample() { return positiveExample; }
    public void setPositiveExample(String positiveExample) { this.positiveExample = positiveExample; }

    public String getNegativeExample() { return negativeExample; }
    public void setNegativeExample(String negativeExample) { this.negativeExample = negativeExample; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
