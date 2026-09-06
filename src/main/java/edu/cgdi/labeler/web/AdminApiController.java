package edu.cgdi.labeler.web;

import edu.cgdi.labeler.service.AssignmentService;
import edu.cgdi.labeler.service.FilingFolderImportService;
import edu.cgdi.labeler.web.dto.BulkAssignmentRequest;
import edu.cgdi.labeler.web.dto.ClearAnnotationsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AssignmentService assignmentService;
    private final FilingFolderImportService importService;

    public AdminApiController(
        AssignmentService assignmentService,
        FilingFolderImportService importService
    ) {
        this.assignmentService = assignmentService;
        this.importService = importService;
    }

    @GetMapping("/users")
    public Object users() {
        return assignmentService.users();
    }

    @GetMapping("/filings")
    public Object filings() {
        return assignmentService.filings();
    }

    @GetMapping("/assignments")
    public Object assignments() {
        return assignmentService.assignments();
    }

    @PostMapping("/assignments")
    public Object applyAssignments(@RequestBody BulkAssignmentRequest request) {
        assignmentService.apply(request);
        return Map.of("saved", true);
    }

    @GetMapping("/import/status")
    public Object importStatus() {
        return importService.getStatus();
    }

    @PostMapping("/import/reload")
    public Object reloadFolder() {
        return importService.importNow();
    }

    @PostMapping("/annotations/clear")
    public Object clearAnnotations(@RequestBody ClearAnnotationsRequest request) {
        int deleted = assignmentService.clearLabels(request);
        return Map.of("deleted", deleted);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
