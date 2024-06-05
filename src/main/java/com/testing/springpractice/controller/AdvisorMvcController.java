package com.testing.springpractice.controller;

import com.testing.springpractice.service.AdvisorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/advisors")
public class AdvisorMvcController {
    private final AdvisorService advisorService;

    public AdvisorMvcController(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    @GetMapping
    public String getAllAdvisorsPage() {
        return "main_web_page";
    }

    @GetMapping("/create")
    public String getCreatePage() {
        return "create_advisor_page";
    }

    @GetMapping("/edit/{id}")
    public String getEditAdvisorPage(final Model model, final @PathVariable Long id) {
        model.addAttribute("advisorEdit", advisorService.findAdvisorById(id));
        return "edit_advisor_page";
    }

    @GetMapping("/{id}/portfolios/page")
    public String getAdvisorsPortfoliosPage(final Model model, final @PathVariable Long id) {
        model.addAttribute("advisorId", id);
        return "portfolio_page";
    }
}
