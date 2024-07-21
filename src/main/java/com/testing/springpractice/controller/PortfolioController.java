package com.testing.springpractice.controller;


import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.service.AdvisorService;
import com.testing.springpractice.service.PortfolioService;
import com.testing.springpractice.util.csv.PortfolioCsvUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private PortfolioService portfolioService;

    private PortfolioRepository portfolioRepository;

    private AdvisorService advisorService;

    public PortfolioController(PortfolioRepository portfolioRepository, PortfolioService portfolioService, AdvisorService advisorService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
        this.advisorService = advisorService;
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
    public ResponseEntity deletePortfolio(final @RequestParam(value = "id") Long id) {
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
        List<PortfolioEntity> portfolios = (List<PortfolioEntity>) portfolioRepository.findAll();
        Path pathToTempPortfolioFile = PortfolioCsvUtil.writePortfolioToCsvServerSide(portfolios);


        try (FileInputStream in = new FileInputStream(String.valueOf(pathToTempPortfolioFile));
             OutputStream out = response.getOutputStream()) {

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + pathToTempPortfolioFile.getFileName() + "\"");
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(pathToTempPortfolioFile)));

            byte[] buffer = new byte[1024];
            int numBytesRead;

            //Encoding issue with other writers (apache comms, guava)
            while ((numBytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numBytesRead);
            }

            out.flush();
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            // Check out PipedOutputStream

            try {
                Files.deleteIfExists(pathToTempPortfolioFile);
            } catch (IOException e) {
                System.err.println("Failed to delete the file: " + pathToTempPortfolioFile.getFileName());
            }

        }
    }

    @GetMapping("/generate_csv_file")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> generateCsvFileResource(HttpServletResponse response) {
        List<PortfolioEntity> portfolios = (List<PortfolioEntity>) portfolioRepository.findAll();
        //Use with small files
        Resource fileResource = new FileSystemResource(PortfolioCsvUtil.writePortfolioToCsvServerSide(portfolios));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header("Content-Disposition", "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

}
