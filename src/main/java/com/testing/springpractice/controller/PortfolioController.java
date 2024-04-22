package com.testing.springpractice.controller;


import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.service.PortfolioService;
import com.testing.springpractice.util.csv.PortfolioCsvUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@RestController
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
    public ResponseEntity createPortfolioWithAssets(@RequestBody PortfolioDTO portfolio) {
        try {
            PortfolioDTO newPortfolio = portfolioService.createPortfolioWithAssets(portfolio);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPortfolio);
        } catch (ResponseStatusException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
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
    public ResponseEntity<?> editPortfolio(@RequestBody PortfolioDTO updatedPortfolio, @PathVariable Long id) {
        try {
            PortfolioDTO updated = portfolioService.updatePortfolio(id, updatedPortfolio);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Portfolio was not updated due to an error: " + e.getMessage());
        }
    }


    @GetMapping(value = "/download_csv", produces = "text/csv")
    public void downloadPortfolios(HttpServletResponse response) {
        response.setHeader("Content-Disposition", "attachment; filename=\"portfolios.csv\"");

        List<PortfolioEntity> portfolios = (List<PortfolioEntity>) portfolioRepository.findAll();
        PortfolioCsvUtil.writePortfoliosToCsv(response, portfolios);
    }

    @GetMapping("/generate_csv")
    public void generateCsvFile(HttpServletResponse response) {
        String filePath = "portfolios.csv";
        List<PortfolioEntity> portfolios = (List<PortfolioEntity>) portfolioRepository.findAll();
        PortfolioCsvUtil.writePortfolioToCsvServerSide(filePath, portfolios);

        FileInputStream in = null;
        OutputStream out = null;
        File file = new File(filePath);
        try {
            in = new FileInputStream(file);
            out = response.getOutputStream();

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

            byte[] buffer = new byte[1024];
            int numBytesRead;

            while ((numBytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numBytesRead);
            }

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file.exists() && !file.delete()) {
                System.err.println("Failed to delete the file: " + file.getPath());
            }
        }
    }

}
