package edu.cgdi.labeler.model;

import java.util.ArrayList;
import java.util.List;

public class DimensionDef {
    private String code;
    private String aspect;
    private String name;
    private String question;
    private String definition;
    private String positiveExample;
    private String negativeExample;
    private String boundaryNote;
    private Integer displayOrder;
    private List<AttributeDef> attributes = new ArrayList<>();

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getAspect() { return aspect; }
    public void setAspect(String aspect) { this.aspect = aspect; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getPositiveExample() { return positiveExample; }
    public void setPositiveExample(String positiveExample) { this.positiveExample = positiveExample; }

    public String getNegativeExample() { return negativeExample; }
    public void setNegativeExample(String negativeExample) { this.negativeExample = negativeExample; }

    public String getBoundaryNote() { return boundaryNote; }
    public void setBoundaryNote(String boundaryNote) { this.boundaryNote = boundaryNote; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public List<AttributeDef> getAttributes() { return attributes; }
    public void setAttributes(List<AttributeDef> attributes) { this.attributes = attributes; }
}
