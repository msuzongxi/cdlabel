package edu.cgdi.labeler.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String root(Authentication authentication) {
        boolean adminOrSupervisor = authentication.getAuthorities().stream().anyMatch(a ->
            a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERVISOR")
        );
        return adminOrSupervisor ? "redirect:/assignments" : "redirect:/label";
    }

    @GetMapping("/label")
    public String filings(Authentication authentication, Model model) {
        addCommon(authentication, model);
        return "filings";
    }

    @GetMapping("/label/filing/{filingId}")
    public String label(
        @PathVariable Long filingId,
        Authentication authentication,
        Model model
    ) {
        addCommon(authentication, model);
        model.addAttribute("filingId", filingId);
        return "label";
    }

    @GetMapping("/assignments")
    public String assignments(Authentication authentication, Model model) {
        addCommon(authentication, model);
        return "assignments";
    }

    @GetMapping("/review")
    public String review(Authentication authentication, Model model) {
        addCommon(authentication, model);
        return "review";
    }

    @GetMapping("/review/paragraphs")
    public String reviewParagraphs(Authentication authentication, Model model) {
        addCommon(authentication, model);
        return "review-paragraphs";
    }

    @GetMapping("/review/filing/{filingId}")
    public String reviewFiling(
        @PathVariable Long filingId,
        Authentication authentication,
        Model model
    ) {
        addCommon(authentication, model);
        model.addAttribute("filingId", filingId);
        return "review-filing";
    }

    private void addCommon(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        boolean canAssign = authentication.getAuthorities().stream().anyMatch(a ->
            a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERVISOR")
        );
        model.addAttribute("canAssign", canAssign);
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a ->
            a.getAuthority().equals("ROLE_ADMIN")
        );
        model.addAttribute("isAdmin", isAdmin);
    }
}
