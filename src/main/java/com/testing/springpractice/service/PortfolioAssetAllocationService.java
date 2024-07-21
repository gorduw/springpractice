package com.testing.springpractice.service;

import com.testing.springpractice.dto.PortfolioAssetAllocationDTO;
import com.testing.springpractice.exception.AllocationExceededException;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.PortfolioAssetAllocationMapper;
import com.testing.springpractice.repository.PortfolioAssetAllocationRepository;
import com.testing.springpractice.repository.entity.PortfolioAssetAllocationEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioAssetAllocationService {

    private final PortfolioAssetAllocationRepository repository;

    public PortfolioAssetAllocationService(PortfolioAssetAllocationRepository repository) {
        this.repository = repository;
    }

    public List<PortfolioAssetAllocationDTO> getAllAllocations() {
        List<PortfolioAssetAllocationDTO> allocations = new ArrayList<>();
        for (PortfolioAssetAllocationEntity entity : repository.findAll()) {
            allocations.add(PortfolioAssetAllocationMapper.INSTANCE.entityToDto(entity));
        }
        return allocations;
    }

    public PortfolioAssetAllocationDTO getAllocation(Long portfolioId, Long assetId) {
        PortfolioAssetAllocationEntity.PortfolioAssetAllocationId id =
                new PortfolioAssetAllocationEntity.PortfolioAssetAllocationId(portfolioId, assetId);
        return repository.findById(id)
                .map(PortfolioAssetAllocationMapper.INSTANCE::entityToDto)
                .orElseThrow(() -> new NotFoundException("PortfolioAssetAllocation", "Portfolio ID and Asset ID", portfolioId + ", " + assetId));
    }

    @Transactional
    public List<PortfolioAssetAllocationDTO> createAllocations(List<PortfolioAssetAllocationDTO> dtos) {
        if (dtos.isEmpty()) {
            throw new IllegalArgumentException("Allocation list cannot be empty");
        }

        Map<Long, List<PortfolioAssetAllocationDTO>> groupedByPortfolio = dtos.stream()
                .collect(Collectors.groupingBy(PortfolioAssetAllocationDTO::getPortfolioId));

        List<PortfolioAssetAllocationDTO> createdAllocations = new ArrayList<>();

        for (Map.Entry<Long, List<PortfolioAssetAllocationDTO>> entry : groupedByPortfolio.entrySet()) {
            Long portfolioId = entry.getKey();
            List<PortfolioAssetAllocationDTO> allocationsForPortfolio = entry.getValue();

            BigDecimal existingAllocationsSum = repository.findByPortfolioId(portfolioId).stream()
                    .map(PortfolioAssetAllocationEntity::getAllocationPercentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);


            BigDecimal newAllocationsSum = allocationsForPortfolio.stream()
                    .map(PortfolioAssetAllocationDTO::getAllocationPercentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);


            if (existingAllocationsSum.add(newAllocationsSum).compareTo(new BigDecimal("100.00")) > 0) {
                throw new AllocationExceededException(portfolioId, "Total allocation percentage for the portfolio exceeds 100%");
            }

            for (PortfolioAssetAllocationDTO dto : allocationsForPortfolio) {
                PortfolioAssetAllocationEntity entity = PortfolioAssetAllocationMapper.INSTANCE.dtoToEntity(dto);
                entity = repository.save(entity);
                createdAllocations.add(PortfolioAssetAllocationMapper.INSTANCE.entityToDto(entity));
            }
        }

        return createdAllocations;
    }

    public void deleteAllocation(Long portfolioId, Long assetId) {
        PortfolioAssetAllocationEntity.PortfolioAssetAllocationId id =
                new PortfolioAssetAllocationEntity.PortfolioAssetAllocationId(portfolioId, assetId);
        repository.deleteById(id);
    }
}