package com.testing.springpractice.controller;


import com.testing.springpractice.model.Portfolio;
import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @PostMapping(consumes = "application/json")
    public ResponseEntity createPortfolioWithAssets(@RequestBody Portfolio portfolio) {
        try {
            Portfolio newPortfolio = portfolioService.createPortfolioWithAssets(portfolio);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPortfolio);
        } catch (ResponseStatusException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String getPortfolioPage(Model model, @PathVariable Long id) {
        Optional<Portfolio> portfolio = portfolioRepository.findById(id);
        model.addAttribute("portfolio", portfolio.get());
        return "portfolio_page";
    }

    @DeleteMapping("/delete")
    public ResponseEntity deletePortfolio(@RequestParam(value = "id") Long id) {
        try {
            Optional<Portfolio> portfolio = portfolioRepository.findById(id);
            if (portfolio.isPresent()) {
                portfolioRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Portfolio deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfolio not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete portfolio: " + e.getMessage());
        }
    }

    @PutMapping(value = "/edit/{id}", consumes = "application/json")
    public ResponseEntity<?> editPortfolio(@RequestBody Portfolio updatedPortfolio, @PathVariable Long id) {
        try {
            Portfolio updated = portfolioService.updatePortfolio(id, updatedPortfolio);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating portfolio");
        }
    }


}
