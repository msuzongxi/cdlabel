package edu.cgdi.labeler.model;

public class FilingSummary {
    private Long filingId;
    private String company;
    private Integer fiscalYear;
    private String filename;
    private String masterCik;
    private Integer totalCount;
    private Integer newCount;
    private Integer modifiedCount;
    private Integer submittedCount;

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
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getNewCount() { return newCount; }
    public void setNewCount(Integer newCount) { this.newCount = newCount; }
    public Integer getModifiedCount() { return modifiedCount; }
    public void setModifiedCount(Integer modifiedCount) { this.modifiedCount = modifiedCount; }
    public Integer getSubmittedCount() { return submittedCount; }
    public void setSubmittedCount(Integer submittedCount) { this.submittedCount = submittedCount; }
}
