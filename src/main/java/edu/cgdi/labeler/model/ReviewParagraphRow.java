package edu.cgdi.labeler.model;

public class ReviewParagraphRow {
    private Long paragraphDbId;
    private String paragraphId;
    private String sectionHint;
    private String headingContext;
    private String text;
    private Integer sortOrder;

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
