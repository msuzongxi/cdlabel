package edu.cgdi.labeler.web.dto;

import java.util.List;

public record ClearAnnotationsRequest(
    List<Long> filingIds,
    List<Long> userIds
) {}
