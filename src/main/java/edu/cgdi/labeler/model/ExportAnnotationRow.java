package edu.cgdi.labeler.model;

import java.time.OffsetDateTime;

public class ExportAnnotationRow {
    private String datasetStage;
    private String batchCode;
    private Long filingId;
    private String filename;
    private String masterCik;
    private String company;
    private Integer fiscalYear;
    private Long paragraphDbId;
    private String paragraphId;
    private String sectionHint;
    private String headingContext;
    private Integer sortOrder;
    private String text;
    private Long userId;
    private String username;
    private Long annotationId;
    private String cyberNexus;
    private String registrantAttribution;
    private String governanceProcess;
    private Boolean eligible;
    private Boolean ontologyGap;
    private String gapNote;
    private String notes;
    private String status;
    private OffsetDateTime submittedAt;

    public String getDatasetStage() { return datasetStage; }
    public void setDatasetStage(String datasetStage) { this.datasetStage = datasetStage; }
    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }
    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getMasterCik() { return masterCik; }
    public void setMasterCik(String masterCik) { this.masterCik = masterCik; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public Integer getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(Integer fiscalYear) { this.fiscalYear = fiscalYear; }
    public Long getParagraphDbId() { return paragraphDbId; }
    public void setParagraphDbId(Long paragraphDbId) { this.paragraphDbId = paragraphDbId; }
    public String getParagraphId() { return paragraphId; }
    public void setParagraphId(String paragraphId) { this.paragraphId = paragraphId; }
    public String getSectionHint() { return sectionHint; }
    public void setSectionHint(String sectionHint) { this.sectionHint = sectionHint; }
    public String getHeadingContext() { return headingContext; }
    public void setHeadingContext(String headingContext) { this.headingContext = headingContext; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getAnnotationId() { return annotationId; }
    public void setAnnotationId(Long annotationId) { this.annotationId = annotationId; }
    public String getCyberNexus() { return cyberNexus; }
    public void setCyberNexus(String cyberNexus) { this.cyberNexus = cyberNexus; }
    public String getRegistrantAttribution() { return registrantAttribution; }
    public void setRegistrantAttribution(String registrantAttribution) { this.registrantAttribution = registrantAttribution; }
    public String getGovernanceProcess() { return governanceProcess; }
    public void setGovernanceProcess(String governanceProcess) { this.governanceProcess = governanceProcess; }
    public Boolean getEligible() { return eligible; }
    public void setEligible(Boolean eligible) { this.eligible = eligible; }
    public Boolean getOntologyGap() { return ontologyGap; }
    public void setOntologyGap(Boolean ontologyGap) { this.ontologyGap = ontologyGap; }
    public String getGapNote() { return gapNote; }
    public void setGapNote(String gapNote) { this.gapNote = gapNote; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
}
