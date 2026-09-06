package edu.cgdi.labeler.model;

public class ParagraphTask {
    private Long id;
    private Long filingId;
    private String paragraphId;
    private String text;
    private String sectionHint;
    private String headingContext;
    private Integer sortOrder;
    private boolean candidate;
    private Integer wordCount;

    private String company;
    private Integer fiscalYear;
    private String filename;
    private String masterCik;
    private String sourcePath;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }

    public String getParagraphId() { return paragraphId; }
    public void setParagraphId(String paragraphId) { this.paragraphId = paragraphId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSectionHint() { return sectionHint; }
    public void setSectionHint(String sectionHint) { this.sectionHint = sectionHint; }

    public String getHeadingContext() { return headingContext; }
    public void setHeadingContext(String headingContext) { this.headingContext = headingContext; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public boolean isCandidate() { return candidate; }
    public void setCandidate(boolean candidate) { this.candidate = candidate; }

    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public Integer getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(Integer fiscalYear) { this.fiscalYear = fiscalYear; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getMasterCik() { return masterCik; }
    public void setMasterCik(String masterCik) { this.masterCik = masterCik; }

    public String getSourcePath() { return sourcePath; }
    public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }
}
