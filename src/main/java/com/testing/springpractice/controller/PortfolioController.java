package com.testing.springpractice.controller;


import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController {

    private PortfolioService portfolioService;

    private PortfolioRepository portfolioRepository;

    public PortfolioController(PortfolioRepository portfolioRepository, PortfolioService portfolioService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
    }

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public ResponseEntity createPortfolioWithAssets(@RequestBody PortfolioEntity portfolioEntity) {
        try {
            PortfolioEntity newPortfolioEntity = portfolioService.createPortfolioWithAssets(portfolioEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPortfolioEntity);
        } catch (ResponseStatusException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String getPortfolioPage(Model model, @PathVariable Long id) {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findById(id);
        model.addAttribute("portfolio", portfolio.get());
        return "portfolio_page";
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity deletePortfolio(@RequestParam(value = "id") Long id) {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findById(id);
        if (portfolio.isPresent()) {
            portfolioRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Portfolio deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfolio not found");
        }
    }

    @PutMapping(value = "/edit/{id}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> editPortfolio(@RequestBody PortfolioEntity updatedPortfolioEntity, @PathVariable Long id) {
        try {
            PortfolioEntity updated = portfolioService.updatePortfolio(id, updatedPortfolioEntity);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Portfolio was not updated due to an error: " + e.getMessage());
        }
    }


}
