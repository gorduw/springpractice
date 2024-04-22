package com.testing.springpractice.controller;


import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/portfolio")
public class PortfolioMvcController {

    private final PortfolioRepository portfolioRepository;

    public PortfolioMvcController(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping("/{id}")
    public String getPortfolioPage(Model model, @PathVariable Long id) {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findById(id);
        portfolio.ifPresent(entity -> model.addAttribute("portfolio", entity));
        return "portfolio_page";
    }
}
