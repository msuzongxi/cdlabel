package edu.cgdi.labeler.web;

import edu.cgdi.labeler.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewApiController {

    private final ReviewService reviewService;

    public ReviewApiController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/filings")
    public Object filings() {
        return reviewService.filings();
    }

    @GetMapping("/paragraphs")
    public Object paragraphs(
        @RequestParam(name = "labelerA", required = false) Long labelerAId,
        @RequestParam(name = "labelerB", required = false) Long labelerBId,
        @RequestParam(name = "stage", required = false, defaultValue = "CALIBRATION") String stage
    ) {
        return reviewService.paragraphQueue(labelerAId, labelerBId, stage);
    }

    @GetMapping("/filings/{filingId}")
    public Object filing(
        @PathVariable Long filingId,
        @RequestParam(name = "labelerA", required = false) Long labelerAId,
        @RequestParam(name = "labelerB", required = false) Long labelerBId
    ) {
        return reviewService.filing(filingId, labelerAId, labelerBId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
