package com.testing.springpractice.service;

import com.testing.springpractice.model.AssetHolding;
import com.testing.springpractice.model.Portfolio;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AdvisorRepository advisorRepository;

    @Autowired
    private AssetService assetService;

    public Portfolio createPortfolioWithAssets(Portfolio portfolio) {
        if (portfolio == null) {
            throw new IllegalArgumentException("Portfolio cannot be null");
        }

        if (advisorRepository.findById(portfolio.getAdvisorId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found with id " + portfolio.getAdvisorId());
        }

        Portfolio portfolioNew = new Portfolio();
        portfolioNew.setName(portfolio.getName());
        portfolioNew.setTimeRange(portfolio.getTimeRange());
        portfolioNew.setRiskProfile(portfolio.getRiskProfile());
        portfolioNew.setAdvisorId(portfolio.getAdvisorId());


        List<Long> assetIds = portfolio.getAssets().stream()
                .map(AssetHolding::getId)
                .collect(Collectors.toList());

        List<AssetHolding> assets = assetService.getAssetsByIds(assetIds);
        portfolio.setAssets(assets);


        return (Portfolio) portfolioRepository.save(portfolioNew);
    }

    public Portfolio updatePortfolio(Long id, Portfolio updatedPortfolio) {
        Portfolio existingPortfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found"));

        if (updatedPortfolio.getAdvisorId() != null &&
                !updatedPortfolio.getAdvisorId().equals(existingPortfolio.getAdvisorId())) {
            throw new IllegalStateException("Portfolio is already assigned to another advisor");
        }

        existingPortfolio.setName(updatedPortfolio.getName());
        existingPortfolio.setTimeRange(updatedPortfolio.getTimeRange());
        existingPortfolio.setRiskProfile(updatedPortfolio.getRiskProfile());
        existingPortfolio.setAdvisorId(updatedPortfolio.getAdvisorId());


        List<Long> assetIds = updatedPortfolio.getAssets().stream()
                .map(AssetHolding::getId)
                .collect(Collectors.toList());

        List<AssetHolding> assets = assetService.getAssetsByIds(assetIds);
        existingPortfolio.setAssets(assets);

        return portfolioRepository.save(existingPortfolio);
    }

}
