package edu.cgdi.labeler.model;

public class ParagraphListItem {
    private Long id;
    private Long filingId;
    private String paragraphId;
    private String sectionHint;
    private String headingContext;
    private Integer sortOrder;
    private Integer wordCount;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }
    public String getParagraphId() { return paragraphId; }
    public void setParagraphId(String paragraphId) { this.paragraphId = paragraphId; }
    public String getSectionHint() { return sectionHint; }
    public void setSectionHint(String sectionHint) { this.sectionHint = sectionHint; }
    public String getHeadingContext() { return headingContext; }
    public void setHeadingContext(String headingContext) { this.headingContext = headingContext; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
