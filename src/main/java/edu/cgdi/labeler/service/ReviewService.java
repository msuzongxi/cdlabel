package edu.cgdi.labeler.service;

import edu.cgdi.labeler.mapper.ReviewMapper;
import edu.cgdi.labeler.model.AssignableUser;
import edu.cgdi.labeler.model.ReviewAnnotationRow;
import edu.cgdi.labeler.model.ReviewAttributeRow;
import edu.cgdi.labeler.model.ReviewDimensionRow;
import edu.cgdi.labeler.model.ReviewFilingRow;
import edu.cgdi.labeler.model.ReviewParagraphRow;
import edu.cgdi.labeler.model.ReviewQueueParagraphRow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final List<String> DIMENSION_CODES = List.of("A1", "A2", "A3", "B1", "B2", "C1");
    private static final Set<String> VALID_STAGES = Set.of("CALIBRATION", "BLIND_PILOT", "FORMAL_VALIDATION", "OTHER", "ALL");

    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    public List<ReviewFilingRow> filings() {
        return reviewMapper.findReviewFilings();
    }

    public List<AssignableUser> comparisonLabelers() {
        return reviewMapper.findComparisonLabelers();
    }

    public Map<String, Object> filing(Long filingId, Long requestedLabelerAId, Long requestedLabelerBId) {
        ReviewFilingRow filing = reviewMapper.findReviewFiling(filingId);
        if (filing == null) {
            throw new IllegalArgumentException("Filing not found: " + filingId);
        }

        List<AssignableUser> availableLabelers = reviewMapper.findAssignedLabelers(filingId);
        Pair pair = resolvePair(availableLabelers, requestedLabelerAId, requestedLabelerBId, true);

        List<ReviewParagraphRow> paragraphs = reviewMapper.findParagraphs(filingId);
        List<ReviewAnnotationRow> allAnnotationRows = reviewMapper.findAnnotations(filingId);
        List<ReviewDimensionRow> dimensionRows = reviewMapper.findDimensions(filingId);
        List<ReviewAttributeRow> attributeRows = reviewMapper.findAttributes(filingId);

        final Long selectedA = pair.labelerAId();
        final Long selectedB = pair.labelerBId();
        List<ReviewAnnotationRow> annotationRows = allAnnotationRows.stream()
            .filter(row -> Objects.equals(row.getUserId(), selectedA) || Objects.equals(row.getUserId(), selectedB))
            .toList();

        Map<Long, List<ReviewAnnotationRow>> annotationsByParagraph = groupAnnotations(annotationRows);
        Map<Long, Set<String>> dimensionsByAnnotation = dimensionsByAnnotation(dimensionRows);
        Map<Long, Map<String, Set<String>>> attributesByAnnotation = attributesByAnnotation(attributeRows);

        int agreementCount = 0;
        int disagreementCount = 0;
        int incompleteCount = 0;
        List<Map<String, Object>> paragraphResults = new ArrayList<>();

        for (ReviewParagraphRow paragraph : paragraphs) {
            List<ReviewAnnotationRow> paragraphRows = annotationsByParagraph
                .getOrDefault(paragraph.getParagraphDbId(), List.of());
            ReviewAnnotationRow rowA = findUserRow(paragraphRows, selectedA);
            ReviewAnnotationRow rowB = findUserRow(paragraphRows, selectedB);

            Comparison comparison = compare(rowA, rowB, dimensionsByAnnotation, attributesByAnnotation);
            if ("AGREEMENT".equals(comparison.status())) agreementCount++;
            else if ("DISAGREEMENT".equals(comparison.status())) disagreementCount++;
            else incompleteCount++;

            List<Map<String, Object>> labels = labelPair(rowA, rowB, dimensionsByAnnotation, attributesByAnnotation);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("paragraphDbId", paragraph.getParagraphDbId());
            item.put("paragraphId", paragraph.getParagraphId());
            item.put("sectionHint", paragraph.getSectionHint());
            item.put("headingContext", paragraph.getHeadingContext());
            item.put("text", paragraph.getText());
            item.put("compareStatus", comparison.status());
            item.put("differences", comparison.differences());
            item.put("differenceCodes", comparison.differenceCodes());
            item.put("labels", labels);
            paragraphResults.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("filing", filing);
        result.put("availableLabelers", availableLabelers);
        result.put("selectedLabelerAId", selectedA);
        result.put("selectedLabelerBId", selectedB);
        result.put("selectedLabelerAName", usernameFor(availableLabelers, selectedA));
        result.put("selectedLabelerBName", usernameFor(availableLabelers, selectedB));
        result.put("labelerWarning", availableLabelers.size() < 2
            ? "At least two LABELER assignments are required for comparison."
            : null);
        result.put("agreementCount", agreementCount);
        result.put("disagreementCount", disagreementCount);
        result.put("incompleteCount", incompleteCount);
        result.put("paragraphs", paragraphResults);
        return result;
    }

    public Map<String, Object> paragraphQueue(
        Long requestedLabelerAId,
        Long requestedLabelerBId,
        String requestedStage
    ) {
        List<AssignableUser> availableLabelers = reviewMapper.findComparisonLabelers();
        Pair pair = resolvePair(availableLabelers, requestedLabelerAId, requestedLabelerBId, false);
        String stage = normalizeStage(requestedStage);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("availableLabelers", availableLabelers);
        result.put("selectedLabelerAId", pair.labelerAId());
        result.put("selectedLabelerBId", pair.labelerBId());
        result.put("selectedLabelerAName", usernameFor(availableLabelers, pair.labelerAId()));
        result.put("selectedLabelerBName", usernameFor(availableLabelers, pair.labelerBId()));
        result.put("stage", stage);

        if (pair.labelerAId() == null || pair.labelerBId() == null) {
            result.put("labelerWarning", "At least two assigned LABELER accounts are required for comparison.");
            result.put("agreementCount", 0);
            result.put("disagreementCount", 0);
            result.put("incompleteCount", 0);
            result.put("bothSubmittedCount", 0);
            result.put("commonFilingCount", 0);
            result.put("paragraphs", List.of());
            return result;
        }

        Long labelerAId = pair.labelerAId();
        Long labelerBId = pair.labelerBId();
        List<ReviewQueueParagraphRow> paragraphs = reviewMapper.findQueueParagraphs(labelerAId, labelerBId, stage);
        List<ReviewAnnotationRow> annotationRows = reviewMapper.findQueueAnnotations(labelerAId, labelerBId, stage);
        List<ReviewDimensionRow> dimensionRows = reviewMapper.findQueueDimensions(labelerAId, labelerBId, stage);
        List<ReviewAttributeRow> attributeRows = reviewMapper.findQueueAttributes(labelerAId, labelerBId, stage);

        Map<Long, List<ReviewAnnotationRow>> annotationsByParagraph = groupAnnotations(annotationRows);
        Map<Long, Set<String>> dimensionsByAnnotation = dimensionsByAnnotation(dimensionRows);
        Map<Long, Map<String, Set<String>>> attributesByAnnotation = attributesByAnnotation(attributeRows);

        int agreementCount = 0;
        int disagreementCount = 0;
        int incompleteCount = 0;
        Set<Long> filingIds = new LinkedHashSet<>();
        List<Map<String, Object>> paragraphResults = new ArrayList<>();

        for (ReviewQueueParagraphRow paragraph : paragraphs) {
            filingIds.add(paragraph.getFilingId());
            List<ReviewAnnotationRow> paragraphRows = annotationsByParagraph
                .getOrDefault(paragraph.getParagraphDbId(), List.of());
            ReviewAnnotationRow rowA = findUserRow(paragraphRows, labelerAId);
            ReviewAnnotationRow rowB = findUserRow(paragraphRows, labelerBId);

            Comparison comparison = compare(rowA, rowB, dimensionsByAnnotation, attributesByAnnotation);
            if ("AGREEMENT".equals(comparison.status())) agreementCount++;
            else if ("DISAGREEMENT".equals(comparison.status())) disagreementCount++;
            else incompleteCount++;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("filingId", paragraph.getFilingId());
            item.put("company", paragraph.getCompany());
            item.put("fiscalYear", paragraph.getFiscalYear());
            item.put("filename", paragraph.getFilename());
            item.put("masterCik", paragraph.getMasterCik());
            item.put("datasetStage", paragraph.getDatasetStage());
            item.put("batchCode", paragraph.getBatchCode());
            item.put("paragraphDbId", paragraph.getParagraphDbId());
            item.put("paragraphId", paragraph.getParagraphId());
            item.put("sectionHint", paragraph.getSectionHint());
            item.put("headingContext", paragraph.getHeadingContext());
            item.put("text", paragraph.getText());
            item.put("compareStatus", comparison.status());
            item.put("differences", comparison.differences());
            item.put("differenceCodes", comparison.differenceCodes());
            item.put("labels", labelPair(rowA, rowB, dimensionsByAnnotation, attributesByAnnotation));
            paragraphResults.add(item);
        }

        int bothSubmittedCount = agreementCount + disagreementCount;
        result.put("labelerWarning", paragraphs.isEmpty()
            ? "No filings in this stage are assigned to both selected labelers."
            : null);
        result.put("agreementCount", agreementCount);
        result.put("disagreementCount", disagreementCount);
        result.put("incompleteCount", incompleteCount);
        result.put("bothSubmittedCount", bothSubmittedCount);
        result.put("commonFilingCount", filingIds.size());
        result.put("totalCount", paragraphs.size());
        result.put("agreementRate", bothSubmittedCount == 0 ? null : (100.0 * agreementCount / bothSubmittedCount));
        result.put("paragraphs", paragraphResults);
        return result;
    }

    private Pair resolvePair(
        List<AssignableUser> labelers,
        Long requestedLabelerAId,
        Long requestedLabelerBId,
        boolean requireAssignedToFiling
    ) {
        Long labelerAId = requestedLabelerAId;
        Long labelerBId = requestedLabelerBId;

        if (labelerAId == null && labelerBId == null && labelers.size() >= 2) {
            labelerAId = labelers.get(0).getId();
            labelerBId = labelers.get(1).getId();
        } else if (labelerAId == null && labelerBId != null) {
            labelerAId = firstOtherLabelerId(labelers, labelerBId);
        } else if (labelerAId != null && labelerBId == null) {
            labelerBId = firstOtherLabelerId(labelers, labelerAId);
        }

        if (labelerAId != null && labelerBId != null && Objects.equals(labelerAId, labelerBId)) {
            throw new IllegalArgumentException("Choose two different labelers to compare.");
        }

        String suffix = requireAssignedToFiling ? " for this filing." : ".";
        validateLabeler(labelers, labelerAId, "Labeler A", suffix);
        validateLabeler(labelers, labelerBId, "Labeler B", suffix);
        return new Pair(labelerAId, labelerBId);
    }

    private String normalizeStage(String requestedStage) {
        String stage = requestedStage == null || requestedStage.isBlank()
            ? "CALIBRATION"
            : requestedStage.trim().toUpperCase();
        if (!VALID_STAGES.contains(stage)) {
            throw new IllegalArgumentException("Unknown review stage: " + requestedStage);
        }
        return stage;
    }

    private Long firstOtherLabelerId(List<AssignableUser> labelers, Long excludedId) {
        return labelers.stream()
            .map(AssignableUser::getId)
            .filter(id -> !Objects.equals(id, excludedId))
            .findFirst()
            .orElse(null);
    }

    private void validateLabeler(List<AssignableUser> labelers, Long userId, String label, String suffix) {
        if (userId == null) return;
        boolean available = labelers.stream().anyMatch(user -> Objects.equals(user.getId(), userId));
        if (!available) {
            throw new IllegalArgumentException(label + " is not an available LABELER" + suffix);
        }
    }

    private String usernameFor(List<AssignableUser> labelers, Long userId) {
        if (userId == null) return null;
        return labelers.stream()
            .filter(user -> Objects.equals(user.getId(), userId))
            .map(AssignableUser::getUsername)
            .findFirst()
            .orElse(null);
    }

    private Map<Long, List<ReviewAnnotationRow>> groupAnnotations(List<ReviewAnnotationRow> rows) {
        return rows.stream().collect(Collectors.groupingBy(
            ReviewAnnotationRow::getParagraphDbId,
            LinkedHashMap::new,
            Collectors.toList()
        ));
    }

    private Map<Long, Set<String>> dimensionsByAnnotation(List<ReviewDimensionRow> rows) {
        Map<Long, Set<String>> result = new LinkedHashMap<>();
        for (ReviewDimensionRow row : rows) {
            result.computeIfAbsent(row.getAnnotationId(), ignored -> new TreeSet<>()).add(row.getDimensionCode());
        }
        return result;
    }

    private Map<Long, Map<String, Set<String>>> attributesByAnnotation(List<ReviewAttributeRow> rows) {
        Map<Long, Map<String, Set<String>>> result = new LinkedHashMap<>();
        for (ReviewAttributeRow row : rows) {
            result
                .computeIfAbsent(row.getAnnotationId(), ignored -> new TreeMap<>())
                .computeIfAbsent(row.getDimensionCode(), ignored -> new TreeSet<>())
                .add(row.getAttributeCode());
        }
        return result;
    }

    private ReviewAnnotationRow findUserRow(List<ReviewAnnotationRow> rows, Long userId) {
        if (userId == null) return null;
        return rows.stream()
            .filter(row -> Objects.equals(row.getUserId(), userId))
            .findFirst()
            .orElse(null);
    }

    private boolean isSubmitted(ReviewAnnotationRow row) {
        return row != null && row.getAnnotationId() != null && "SUBMITTED".equals(row.getStatus());
    }

    private List<Map<String, Object>> labelPair(
        ReviewAnnotationRow rowA,
        ReviewAnnotationRow rowB,
        Map<Long, Set<String>> dimensionsByAnnotation,
        Map<Long, Map<String, Set<String>>> attributesByAnnotation
    ) {
        List<Map<String, Object>> labels = new ArrayList<>();
        if (rowA != null) labels.add(labelMap(rowA, dimensionsByAnnotation, attributesByAnnotation));
        if (rowB != null) labels.add(labelMap(rowB, dimensionsByAnnotation, attributesByAnnotation));
        return labels;
    }

    private Map<String, Object> labelMap(
        ReviewAnnotationRow row,
        Map<Long, Set<String>> dimensionsByAnnotation,
        Map<Long, Map<String, Set<String>>> attributesByAnnotation
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", row.getUserId());
        result.put("username", row.getUsername());
        result.put("annotationId", row.getAnnotationId());
        result.put("status", row.getAnnotationId() == null ? "NEW" : row.getStatus());
        result.put("gateDecision", gateDecision(row));
        result.put("cyberNexus", row.getCyberNexus());
        result.put("registrantAttribution", row.getRegistrantAttribution());
        result.put("governanceProcess", row.getGovernanceProcess());
        result.put("eligible", row.getEligible());
        result.put("ontologyGap", Boolean.TRUE.equals(row.getOntologyGap()));
        result.put("gapNote", row.getGapNote());
        result.put("notes", row.getNotes());
        result.put("submittedAt", row.getSubmittedAt());

        List<Map<String, Object>> dimensions = new ArrayList<>();
        if (row.getAnnotationId() != null) {
            Set<String> codes = dimensionsByAnnotation.getOrDefault(row.getAnnotationId(), Set.of());
            Map<String, Set<String>> attrs = attributesByAnnotation.getOrDefault(row.getAnnotationId(), Map.of());
            for (String code : codes) {
                Map<String, Object> d = new LinkedHashMap<>();
                d.put("code", code);
                d.put("attributes", attrs.getOrDefault(code, Set.of()));
                dimensions.add(d);
            }
        }
        result.put("dimensions", dimensions);
        return result;
    }

    private Comparison compare(
        ReviewAnnotationRow a,
        ReviewAnnotationRow b,
        Map<Long, Set<String>> dimensionsByAnnotation,
        Map<Long, Map<String, Set<String>>> attributesByAnnotation
    ) {
        if (!isSubmitted(a) || !isSubmitted(b)) {
            return new Comparison("INCOMPLETE", List.of(), List.of());
        }

        List<String> differences = new ArrayList<>();
        LinkedHashSet<String> differenceCodes = new LinkedHashSet<>();

        if (!Objects.equals(gateDecision(a), gateDecision(b))) {
            differences.add("Eligibility / gates");
            differenceCodes.add("ELIGIBILITY");
        }
        if (!Objects.equals(Boolean.TRUE.equals(a.getOntologyGap()), Boolean.TRUE.equals(b.getOntologyGap()))) {
            differences.add("Ontology gap");
            differenceCodes.add("ONTOLOGY_GAP");
        }

        Set<String> aDims = dimensionsByAnnotation.getOrDefault(a.getAnnotationId(), Set.of());
        Set<String> bDims = dimensionsByAnnotation.getOrDefault(b.getAnnotationId(), Set.of());
        Map<String, Set<String>> aAttrs = attributesByAnnotation.getOrDefault(a.getAnnotationId(), Map.of());
        Map<String, Set<String>> bAttrs = attributesByAnnotation.getOrDefault(b.getAnnotationId(), Map.of());

        for (String code : DIMENSION_CODES) {
            boolean aPresent = aDims.contains(code);
            boolean bPresent = bDims.contains(code);
            if (aPresent != bPresent) {
                differences.add(code + " dimension");
                differenceCodes.add(code);
            }
            Set<String> attrsA = aAttrs.getOrDefault(code, Set.of());
            Set<String> attrsB = bAttrs.getOrDefault(code, Set.of());
            if (!attrsA.equals(attrsB)) {
                differences.add(code + " attributes");
                differenceCodes.add(code);
                differenceCodes.add("ATTRIBUTES");
            }
        }

        if (differences.isEmpty()) {
            return new Comparison("AGREEMENT", List.of(), List.of());
        }
        return new Comparison("DISAGREEMENT", differences, new ArrayList<>(differenceCodes));
    }

    private String gateDecision(ReviewAnnotationRow row) {
        if (row == null || row.getAnnotationId() == null) return null;
        if ("YES".equals(row.getCyberNexus())
            && "YES".equals(row.getRegistrantAttribution())
            && "YES".equals(row.getGovernanceProcess())) return "PASS";
        if ("NO".equals(row.getCyberNexus())) return "FAIL1";
        if ("YES".equals(row.getCyberNexus())
            && "NO".equals(row.getRegistrantAttribution())) return "FAIL2";
        if ("YES".equals(row.getCyberNexus())
            && "YES".equals(row.getRegistrantAttribution())
            && "NO".equals(row.getGovernanceProcess())) return "FAIL3";
        return null;
    }

    private record Pair(Long labelerAId, Long labelerBId) {}
    private record Comparison(String status, List<String> differences, List<String> differenceCodes) {}
}
