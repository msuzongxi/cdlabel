package edu.cgdi.labeler.model;

public class ReviewQueueParagraphRow {
    private Long filingId;
    private String company;
    private Integer fiscalYear;
    private String filename;
    private String masterCik;
    private String datasetStage;
    private String batchCode;
    private Long paragraphDbId;
    private String paragraphId;
    private String sectionHint;
    private String headingContext;
    private String text;
    private Integer sortOrder;

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
    public Long getParagraphDbId() { return paragraphDbId; }
    public void setParagraphDbId(Long paragraphDbId) { this.paragraphDbId = paragraphDbId; }
    public String getParagraphId() { return paragraphId; }
    public void setParagraphId(String paragraphId) { this.paragraphId = paragraphId; }
    public String getSectionHint() { return sectionHint; }
    public void setSectionHint(String sectionHint) { this.sectionHint = sectionHint; }
    public String getHeadingContext() { return headingContext; }
    public void setHeadingContext(String headingContext) { this.headingContext = headingContext; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
