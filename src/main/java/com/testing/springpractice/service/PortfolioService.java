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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Portfolio is missing");
        }

        if (advisorRepository.findById(portfolio.getAdvisor().getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found with id " + portfolio.getAdvisor().getId());
        }

        Portfolio portfolioNew = new Portfolio();
        portfolioNew.setName(portfolio.getName());
        portfolioNew.setTimeRange(portfolio.getTimeRange());
        portfolioNew.setRiskProfile(portfolio.getRiskProfile());
        portfolioNew.setAdvisor(portfolio.getAdvisor());


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


        existingPortfolio.setName(updatedPortfolio.getName());
        existingPortfolio.setTimeRange(updatedPortfolio.getTimeRange());
        existingPortfolio.setRiskProfile(updatedPortfolio.getRiskProfile());
        existingPortfolio.setAdvisor(updatedPortfolio.getAdvisor());


        List<Long> assetIds = updatedPortfolio.getAssets().stream()
                .map(AssetHolding::getId)
                .collect(Collectors.toList());

        List<AssetHolding> assets = assetService.getAssetsByIds(assetIds);
        existingPortfolio.setAssets(assets);

        return portfolioRepository.save(existingPortfolio);
    }

}
