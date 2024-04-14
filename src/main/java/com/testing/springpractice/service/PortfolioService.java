package com.testing.springpractice.service;

import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.AssetRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AdvisorRepository advisorRepository;
    private final AssetService assetService;
    private final AssetRepository assetRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            AdvisorRepository advisorRepository,
                            AssetService assetService,
                            AssetRepository assetRepository) {
        this.portfolioRepository = portfolioRepository;
        this.advisorRepository = advisorRepository;
        this.assetService = assetService;
        this.assetRepository = assetRepository;
    }

    public PortfolioEntity createPortfolioWithAssets(final PortfolioEntity portfolioEntity) {
        if (portfolioEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Portfolio is missing");
        }

        if (advisorRepository.findById(portfolioEntity.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found with id " + portfolioEntity.getId());
        }

        PortfolioEntity portfolioEntityNew = new PortfolioEntity();
        portfolioEntityNew.setName(portfolioEntity.getName());
        portfolioEntityNew.setTimeRange(portfolioEntity.getTimeRange());
        portfolioEntityNew.setRiskProfile(portfolioEntity.getRiskProfile());
        portfolioEntityNew.setAdvisorEntity(portfolioEntity.getAdvisorEntity());

        portfolioEntityNew.setAssets(getAssetsFromPayload(portfolioEntity));


        return (PortfolioEntity) portfolioRepository.save(portfolioEntityNew);
    }

    public PortfolioEntity updatePortfolio(final Long id, final PortfolioEntity updatedPortfolioEntity) {
        PortfolioEntity existingPortfolioEntity = portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found"));


        existingPortfolioEntity.setName(updatedPortfolioEntity.getName());
        existingPortfolioEntity.setTimeRange(updatedPortfolioEntity.getTimeRange());
        existingPortfolioEntity.setRiskProfile(updatedPortfolioEntity.getRiskProfile());
        existingPortfolioEntity.setAdvisorEntity(updatedPortfolioEntity.getAdvisorEntity());

        existingPortfolioEntity.setAssets(getAssetsFromPayload(updatedPortfolioEntity));

        return portfolioRepository.save(existingPortfolioEntity);
    }


    public List<AssetHoldingEntity> getAssetsFromPayload(final PortfolioEntity portfolioEntity) {
        List<AssetHoldingEntity> listOfAssets = new ArrayList<>();
        portfolioEntity.getAssets().forEach(asset -> {
            Optional<AssetHoldingEntity> optionalAsset = assetRepository.findById(asset.getId());
            AssetHoldingEntity foundAsset = optionalAsset.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found with id " + asset.getId()));
            listOfAssets.add(foundAsset);
        });
        return listOfAssets;
    }


}
