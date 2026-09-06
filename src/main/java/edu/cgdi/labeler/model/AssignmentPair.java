package edu.cgdi.labeler.model;

public class AssignmentPair {
    private Long filingId;
    private Long userId;
    private String username;
    private String assignmentType;
    private String batchCode;

    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAssignmentType() { return assignmentType; }
    public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }
    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }
}
