package edu.cgdi.labeler.service;

import edu.cgdi.labeler.mapper.LabelingMapper;
import edu.cgdi.labeler.mapper.UserMapper;
import edu.cgdi.labeler.model.AnnotationAttributeRecord;
import edu.cgdi.labeler.model.AnnotationDimensionRecord;
import edu.cgdi.labeler.model.AnnotationRecord;
import edu.cgdi.labeler.model.FilingSummary;
import edu.cgdi.labeler.model.GateValue;
import edu.cgdi.labeler.model.ParagraphListItem;
import edu.cgdi.labeler.model.ParagraphTask;
import edu.cgdi.labeler.web.dto.SaveAnnotationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LabelingService {

    private final LabelingMapper labelingMapper;
    private final UserMapper userMapper;
    private final OntologyService ontologyService;

    public LabelingService(
        LabelingMapper labelingMapper,
        UserMapper userMapper,
        OntologyService ontologyService
    ) {
        this.labelingMapper = labelingMapper;
        this.userMapper = userMapper;
        this.ontologyService = ontologyService;
    }

    public List<FilingSummary> myFilings(String username) {
        return labelingMapper.findMyFilings(username);
    }

    public FilingSummary myFiling(String username, Long filingId) {
        FilingSummary filing = labelingMapper.findMyFiling(username, filingId);
        if (filing == null) {
            throw new IllegalArgumentException("Filing not found or not assigned to this labeler.");
        }
        return filing;
    }

    public List<ParagraphListItem> paragraphs(String username, Long filingId, String status) {
        String normalized = normalizeStatus(status);
        myFiling(username, filingId);
        return labelingMapper.findParagraphsByStatus(username, filingId, normalized);
    }

    public List<ParagraphListItem> paragraphQueue(String username, String status, String stage) {
        String normalizedStatus = normalizeStatus(status);
        String normalizedStage = normalizeStage(stage);
        return labelingMapper.findParagraphQueueByStatus(username, normalizedStatus, normalizedStage);
    }

    public Map<String, Object> paragraphDetail(
        String username,
        Long filingId,
        Long paragraphDbId
    ) {
        ParagraphTask task = labelingMapper.findAccessibleParagraph(username, filingId, paragraphDbId);
        if (task == null) {
            throw new IllegalArgumentException("Paragraph not found or not assigned to this labeler.");
        }

        AnnotationRecord annotation = labelingMapper.findAnnotation(username, paragraphDbId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paragraph", task);

        if (annotation == null) {
            result.put("status", "NEW");
            result.put("annotation", null);
            return result;
        }

        List<AnnotationDimensionRecord> dimensions = labelingMapper.findAnnotationDimensions(annotation.getId());
        List<AnnotationAttributeRecord> attributes = labelingMapper.findAnnotationAttributes(annotation.getId());

        Map<Long, List<AnnotationAttributeRecord>> attrsByDimensionId = new LinkedHashMap<>();
        for (AnnotationAttributeRecord attribute : attributes) {
            attrsByDimensionId
                .computeIfAbsent(attribute.getAnnotationDimensionId(), ignored -> new ArrayList<>())
                .add(attribute);
        }

        List<Map<String, Object>> dimensionDtos = new ArrayList<>();
        for (AnnotationDimensionRecord d : dimensions) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", d.getId());
            dto.put("dimensionCode", d.getDimensionCode());
            dto.put("evidenceText", d.getEvidenceText());
            dto.put("evidenceStart", d.getEvidenceStart());
            dto.put("evidenceEnd", d.getEvidenceEnd());
            dto.put("confidence", d.getConfidence());
            dto.put("ambiguityNote", d.getAmbiguityNote());
            dto.put("attributes", attrsByDimensionId.getOrDefault(d.getId(), List.of()));
            dimensionDtos.add(dto);
        }

        Map<String, Object> annotationDto = new LinkedHashMap<>();
        annotationDto.put("id", annotation.getId());
        annotationDto.put("cyberNexus", annotation.getCyberNexus());
        annotationDto.put("registrantAttribution", annotation.getRegistrantAttribution());
        annotationDto.put("governanceProcess", annotation.getGovernanceProcess());
        annotationDto.put("eligible", annotation.getEligible());
        annotationDto.put("ontologyGap", annotation.isOntologyGap());
        annotationDto.put("gapNote", annotation.getGapNote());
        annotationDto.put("notes", annotation.getNotes());
        annotationDto.put("status", annotation.getStatus());
        annotationDto.put("updatedAt", annotation.getUpdatedAt());
        annotationDto.put("submittedAt", annotation.getSubmittedAt());
        annotationDto.put("dimensions", dimensionDtos);

        result.put("status", annotation.getStatus());
        result.put("annotation", annotationDto);
        return result;
    }

    @Transactional
    public String save(
        Long filingId,
        Long paragraphDbId,
        String username,
        SaveAnnotationRequest request,
        boolean submit
    ) {
        ParagraphTask task = labelingMapper.findAccessibleParagraph(username, filingId, paragraphDbId);
        if (task == null) {
            throw new IllegalArgumentException("Paragraph not found or not assigned to this labeler.");
        }

        Long labelerId = userMapper.findIdByUsername(username);
        if (labelerId == null) {
            throw new IllegalArgumentException("Unknown labeler.");
        }

        boolean passAllGates = request.cyberNexus() == GateValue.YES
            && request.registrantAttribution() == GateValue.YES
            && request.governanceProcess() == GateValue.YES;

        boolean failGate1 = request.cyberNexus() == GateValue.NO;
        boolean failGate2 = request.cyberNexus() == GateValue.YES
            && request.registrantAttribution() == GateValue.NO;
        boolean failGate3 = request.cyberNexus() == GateValue.YES
            && request.registrantAttribution() == GateValue.YES
            && request.governanceProcess() == GateValue.NO;

        boolean decisionComplete = passAllGates || failGate1 || failGate2 || failGate3;
        Boolean eligible = passAllGates ? Boolean.TRUE : (decisionComplete ? Boolean.FALSE : null);

        List<SaveAnnotationRequest.DimensionRequest> dimensions =
            request.dimensions() == null ? List.of() : request.dimensions();

        if (submit && !decisionComplete) {
            throw new IllegalArgumentException(
                "Choose one eligibility outcome: pass all three gates or the first failed gate."
            );
        }

        if (Boolean.FALSE.equals(eligible) && (!dimensions.isEmpty() || request.ontologyGap())) {
            throw new IllegalArgumentException(
                "Dimensions and ontology-gap coding are only allowed when all three gates are YES."
            );
        }

        if (submit && Boolean.TRUE.equals(eligible)) {
            if (dimensions.isEmpty() && !request.ontologyGap()) {
                throw new IllegalArgumentException(
                    "Select at least one dimension, or mark this as an ontology gap."
                );
            }
            if (request.ontologyGap() && (request.gapNote() == null || request.gapNote().isBlank())) {
                throw new IllegalArgumentException("Briefly describe the ontology gap before submitting.");
            }
            for (SaveAnnotationRequest.DimensionRequest d : dimensions) {
                boolean hasAttribute = d.attributes() != null
                    && d.attributes().stream().anyMatch(SaveAnnotationRequest.AttributeRequest::present);
                if (!hasAttribute) {
                    throw new IllegalArgumentException(
                        "Select at least one explicit attribute for " + d.dimensionCode() + "."
                    );
                }
            }
        }

        String status = submit ? "SUBMITTED" : "MODIFIED";
        boolean ontologyGap = Boolean.TRUE.equals(eligible) && request.ontologyGap();

        Long annotationId = labelingMapper.upsertAnnotation(
            paragraphDbId,
            labelerId,
            request.cyberNexus() == null ? null : request.cyberNexus().name(),
            request.registrantAttribution() == null ? null : request.registrantAttribution().name(),
            request.governanceProcess() == null ? null : request.governanceProcess().name(),
            eligible,
            ontologyGap,
            ontologyGap ? request.gapNote() : null,
            request.notes(),
            status
        );

        labelingMapper.deleteDimensions(annotationId);

        if (Boolean.TRUE.equals(eligible) && !ontologyGap) {
            for (SaveAnnotationRequest.DimensionRequest d : dimensions) {
                ontologyService.assertDimension(d.dimensionCode());

                Long annotationDimensionId = labelingMapper.insertDimension(
                    annotationId,
                    d.dimensionCode(),
                    d.evidenceText() == null ? "" : d.evidenceText(),
                    d.evidenceStart(),
                    d.evidenceEnd(),
                    d.confidence() == null ? "MEDIUM" : d.confidence().name(),
                    d.ambiguityNote()
                );

                if (d.attributes() != null) {
                    for (SaveAnnotationRequest.AttributeRequest a : d.attributes()) {
                        ontologyService.assertAttribute(a.attributeCode(), d.dimensionCode());
                        labelingMapper.insertAttribute(
                            annotationDimensionId,
                            a.attributeCode(),
                            a.present(),
                            a.valueText()
                        );
                    }
                }
            }
        }

        return status;
    }

    private String normalizeStage(String stage) {
        if (stage == null || stage.isBlank()) return "CALIBRATION";
        String normalized = stage.trim().toUpperCase();
        if (!normalized.equals("CALIBRATION")
            && !normalized.equals("BLIND_PILOT")
            && !normalized.equals("FORMAL_VALIDATION")
            && !normalized.equals("OTHER")
            && !normalized.equals("ALL")) {
            throw new IllegalArgumentException("Unknown dataset stage: " + stage);
        }
        return normalized;
    }

    private String normalizeStatus(String status) {
        if (status == null) return "NEW";
        String normalized = status.trim().toUpperCase();
        if (!normalized.equals("NEW")
            && !normalized.equals("MODIFIED")
            && !normalized.equals("SUBMITTED")) {
            throw new IllegalArgumentException("Unknown paragraph status: " + status);
        }
        return normalized;
    }
}
