package edu.cgdi.labeler.model;

public class ExportDimensionRow {
    private Long annotationId;
    private String dimensionCode;
    private String evidenceText;
    private Integer evidenceStart;
    private Integer evidenceEnd;
    private String confidence;
    private String ambiguityNote;

    public Long getAnnotationId() { return annotationId; }
    public void setAnnotationId(Long annotationId) { this.annotationId = annotationId; }
    public String getDimensionCode() { return dimensionCode; }
    public void setDimensionCode(String dimensionCode) { this.dimensionCode = dimensionCode; }
    public String getEvidenceText() { return evidenceText; }
    public void setEvidenceText(String evidenceText) { this.evidenceText = evidenceText; }
    public Integer getEvidenceStart() { return evidenceStart; }
    public void setEvidenceStart(Integer evidenceStart) { this.evidenceStart = evidenceStart; }
    public Integer getEvidenceEnd() { return evidenceEnd; }
    public void setEvidenceEnd(Integer evidenceEnd) { this.evidenceEnd = evidenceEnd; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public String getAmbiguityNote() { return ambiguityNote; }
    public void setAmbiguityNote(String ambiguityNote) { this.ambiguityNote = ambiguityNote; }
}
