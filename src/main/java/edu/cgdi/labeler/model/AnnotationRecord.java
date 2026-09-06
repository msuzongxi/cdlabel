package edu.cgdi.labeler.model;

import java.time.OffsetDateTime;

public class AnnotationRecord {
    private Long id;
    private String cyberNexus;
    private String registrantAttribution;
    private String governanceProcess;
    private Boolean eligible;
    private boolean ontologyGap;
    private String gapNote;
    private String notes;
    private String sourceMode;
    private String status;
    private OffsetDateTime updatedAt;
    private OffsetDateTime submittedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCyberNexus() { return cyberNexus; }
    public void setCyberNexus(String cyberNexus) { this.cyberNexus = cyberNexus; }
    public String getRegistrantAttribution() { return registrantAttribution; }
    public void setRegistrantAttribution(String registrantAttribution) { this.registrantAttribution = registrantAttribution; }
    public String getGovernanceProcess() { return governanceProcess; }
    public void setGovernanceProcess(String governanceProcess) { this.governanceProcess = governanceProcess; }
    public Boolean getEligible() { return eligible; }
    public void setEligible(Boolean eligible) { this.eligible = eligible; }
    public boolean isOntologyGap() { return ontologyGap; }
    public void setOntologyGap(boolean ontologyGap) { this.ontologyGap = ontologyGap; }
    public String getGapNote() { return gapNote; }
    public void setGapNote(String gapNote) { this.gapNote = gapNote; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getSourceMode() { return sourceMode; }
    public void setSourceMode(String sourceMode) { this.sourceMode = sourceMode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
}
