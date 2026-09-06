package edu.cgdi.labeler.web;

import edu.cgdi.labeler.service.ExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ExportController {

    private static final MediaType CSV_UTF8 = MediaType.parseMediaType("text/csv;charset=UTF-8");

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/exports")
    public String exports(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("labelers", exportService.labelers());
        model.addAttribute("isAdmin", true);
        return "exports";
    }

    @GetMapping("/exports/csv")
    public ResponseEntity<byte[]> csv(
        @RequestParam(name = "stage", defaultValue = "CALIBRATION") String stage,
        @RequestParam(name = "labelerId", required = false) Long labelerId,
        @RequestParam(name = "status", defaultValue = "SUBMITTED") String status
    ) {
        ExportService.ExportResult result = exportService.exportCsv(stage, labelerId, status);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(CSV_UTF8);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename(result.filename())
            .build());
        headers.setContentLength(result.bytes().length);
        headers.add("X-CGDI-Export-Rows", Integer.toString(result.rowCount()));

        return ResponseEntity.ok()
            .headers(headers)
            .body(result.bytes());
    }
}
