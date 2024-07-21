package com.testing.springpractice.repository;

import com.testing.springpractice.repository.entity.PortfolioAssetAllocationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PortfolioAssetAllocationRepository extends CrudRepository<PortfolioAssetAllocationEntity, PortfolioAssetAllocationEntity.PortfolioAssetAllocationId> {
    List<PortfolioAssetAllocationEntity> findByPortfolioId(Long portfolioId);
    List<PortfolioAssetAllocationEntity> findByAssetId(Long assetId);
}
