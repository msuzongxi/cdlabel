package edu.cgdi.labeler.web.dto;

import edu.cgdi.labeler.model.Confidence;
import edu.cgdi.labeler.model.GateValue;

import java.util.List;

public record SaveAnnotationRequest(
    GateValue cyberNexus,
    GateValue registrantAttribution,
    GateValue governanceProcess,
    boolean ontologyGap,
    String gapNote,
    String notes,
    List<DimensionRequest> dimensions
) {
    public record DimensionRequest(
        String dimensionCode,
        String evidenceText,
        Integer evidenceStart,
        Integer evidenceEnd,
        Confidence confidence,
        String ambiguityNote,
        List<AttributeRequest> attributes
    ) {}

    public record AttributeRequest(
        String attributeCode,
        boolean present,
        String valueText
    ) {}
}
