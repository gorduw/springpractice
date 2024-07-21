package com.testing.springpractice.controller;

import com.testing.springpractice.dto.PortfolioAssetAllocationDTO;
import com.testing.springpractice.service.PortfolioAssetAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio-asset-allocations")
public class PortfolioAssetAllocationController {

    private final PortfolioAssetAllocationService service;

    public PortfolioAssetAllocationController(PortfolioAssetAllocationService service) {
        this.service = service;
    }

    @GetMapping
    public List<PortfolioAssetAllocationDTO> getAllAllocations() {
        return service.getAllAllocations();
    }

    @GetMapping("/{portfolioId}/{assetId}")
    public PortfolioAssetAllocationDTO getAllocation(@PathVariable Long portfolioId, @PathVariable Long assetId) {
        return service.getAllocation(portfolioId, assetId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<PortfolioAssetAllocationDTO> createAllocations(@RequestBody List<PortfolioAssetAllocationDTO> dtos) {
        return service.createAllocations(dtos);
    }

    @DeleteMapping("/{portfolioId}/{assetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllocation(@PathVariable Long portfolioId, @PathVariable Long assetId) {
        service.deleteAllocation(portfolioId, assetId);
    }
}