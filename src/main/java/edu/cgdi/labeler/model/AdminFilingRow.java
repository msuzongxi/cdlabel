package edu.cgdi.labeler.model;

public class AdminFilingRow {
    private Long filingId;
    private String company;
    private Integer fiscalYear;
    private String filename;
    private String masterCik;
    private Integer paragraphCount;
    private Integer assignmentCount;
    private String datasetStage;

    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public Integer getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(Integer fiscalYear) { this.fiscalYear = fiscalYear; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getMasterCik() { return masterCik; }
    public void setMasterCik(String masterCik) { this.masterCik = masterCik; }
    public Integer getParagraphCount() { return paragraphCount; }
    public void setParagraphCount(Integer paragraphCount) { this.paragraphCount = paragraphCount; }
    public Integer getAssignmentCount() { return assignmentCount; }
    public void setAssignmentCount(Integer assignmentCount) { this.assignmentCount = assignmentCount; }
    public String getDatasetStage() { return datasetStage; }
    public void setDatasetStage(String datasetStage) { this.datasetStage = datasetStage; }
}
