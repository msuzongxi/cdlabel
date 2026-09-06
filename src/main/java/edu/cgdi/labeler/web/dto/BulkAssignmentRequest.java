package edu.cgdi.labeler.web.dto;

import java.util.List;

public record BulkAssignmentRequest(
    List<Long> filingIds,
    List<Long> userIds,
    String batchCode,
    String action
) {}
