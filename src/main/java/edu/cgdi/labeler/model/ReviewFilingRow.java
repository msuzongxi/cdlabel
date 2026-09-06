package edu.cgdi.labeler.model;

public class ReviewFilingRow {
    private Long filingId;
    private String company;
    private Integer fiscalYear;
    private String filename;
    private String masterCik;
    private String datasetStage;
    private String batchCode;
    private Integer totalCount;
    private Integer labelerCount;
    private Integer submittedCount;
    private String labelers;

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
    public String getDatasetStage() { return datasetStage; }
    public void setDatasetStage(String datasetStage) { this.datasetStage = datasetStage; }
    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getLabelerCount() { return labelerCount; }
    public void setLabelerCount(Integer labelerCount) { this.labelerCount = labelerCount; }
    public Integer getSubmittedCount() { return submittedCount; }
    public void setSubmittedCount(Integer submittedCount) { this.submittedCount = submittedCount; }
    public String getLabelers() { return labelers; }
    public void setLabelers(String labelers) { this.labelers = labelers; }
}
