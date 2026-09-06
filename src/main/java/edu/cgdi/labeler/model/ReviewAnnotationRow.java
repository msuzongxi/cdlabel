package edu.cgdi.labeler.model;

import java.time.OffsetDateTime;

public class ReviewAnnotationRow {
    private Long paragraphDbId;
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

    public Long getParagraphDbId() { return paragraphDbId; }
    public void setParagraphDbId(Long paragraphDbId) { this.paragraphDbId = paragraphDbId; }
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
