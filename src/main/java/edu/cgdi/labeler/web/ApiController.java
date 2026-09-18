package edu.cgdi.labeler.web;

import edu.cgdi.labeler.service.LabelingService;
import edu.cgdi.labeler.service.OntologyService;
import edu.cgdi.labeler.web.dto.SaveAnnotationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final LabelingService labelingService;
    private final OntologyService ontologyService;

    public ApiController(LabelingService labelingService, OntologyService ontologyService) {
        this.labelingService = labelingService;
        this.ontologyService = ontologyService;
    }

    @GetMapping("/ontology")
    public Object ontology() {
        return ontologyService.getOntology();
    }

    @GetMapping("/my/filings")
    public Object myFilings(Authentication authentication) {
        return labelingService.myFilings(authentication.getName());
    }

    @GetMapping("/my/filings/{filingId}")
    public Object myFiling(
        @PathVariable Long filingId,
        Authentication authentication
    ) {
        return labelingService.myFiling(authentication.getName(), filingId);
    }

    @GetMapping("/my/paragraphs")
    public Object paragraphQueue(
        @RequestParam(defaultValue = "NEW") String status,
        @RequestParam(defaultValue = "CALIBRATION") String stage,
        Authentication authentication
    ) {
        return labelingService.paragraphQueue(authentication.getName(), status, stage);
    }

    @GetMapping("/my/filings/{filingId}/paragraphs")
    public Object paragraphs(
        @PathVariable Long filingId,
        @RequestParam(defaultValue = "NEW") String status,
        Authentication authentication
    ) {
        return labelingService.paragraphs(authentication.getName(), filingId, status);
    }

    @GetMapping("/my/filings/{filingId}/paragraphs/{paragraphDbId}")
    public Object paragraph(
        @PathVariable Long filingId,
        @PathVariable Long paragraphDbId,
        Authentication authentication
    ) {
        return labelingService.paragraphDetail(
            authentication.getName(), filingId, paragraphDbId
        );
    }

    @PostMapping("/my/filings/{filingId}/paragraphs/{paragraphDbId}/save")
    public Object save(
        @PathVariable Long filingId,
        @PathVariable Long paragraphDbId,
        @RequestBody SaveAnnotationRequest request,
        Authentication authentication
    ) {
        String status = labelingService.save(
            filingId, paragraphDbId, authentication.getName(), request, false
        );
        return responseWithProgress(authentication.getName(), filingId, status);
    }

    @PostMapping("/my/filings/{filingId}/paragraphs/{paragraphDbId}/submit")
    public Object submit(
        @PathVariable Long filingId,
        @PathVariable Long paragraphDbId,
        @RequestBody SaveAnnotationRequest request,
        Authentication authentication
    ) {
        String status = labelingService.save(
            filingId, paragraphDbId, authentication.getName(), request, true
        );
        return responseWithProgress(authentication.getName(), filingId, status);
    }

    private Map<String, Object> responseWithProgress(
        String username,
        Long filingId,
        String status
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("saved", true);
        result.put("status", status);
        result.put("filing", labelingService.myFiling(username, filingId));
        return result;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
