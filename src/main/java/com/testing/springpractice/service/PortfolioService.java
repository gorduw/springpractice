package com.testing.springpractice.service;

import com.testing.springpractice.model.AssetHolding;
import com.testing.springpractice.model.Portfolio;
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

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AdvisorRepository advisorRepository;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetRepository assetRepository;

    public Portfolio createPortfolioWithAssets(Portfolio portfolio) {
        if (portfolio == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Portfolio is missing");
        }

        if (advisorRepository.findById(portfolio.getAdvisorId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found with id " + portfolio.getAdvisorId());
        }

        Portfolio portfolioNew = new Portfolio();
        portfolioNew.setName(portfolio.getName());
        portfolioNew.setTimeRange(portfolio.getTimeRange());
        portfolioNew.setRiskProfile(portfolio.getRiskProfile());
        portfolioNew.setAdvisorId(portfolio.getAdvisorId());

        portfolioNew.setAssets(getAssetsFromPayload(portfolio));


        return (Portfolio) portfolioRepository.save(portfolioNew);
    }

    public Portfolio updatePortfolio(Long id, Portfolio updatedPortfolio) {
        Portfolio existingPortfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found"));


        existingPortfolio.setName(updatedPortfolio.getName());
        existingPortfolio.setTimeRange(updatedPortfolio.getTimeRange());
        existingPortfolio.setRiskProfile(updatedPortfolio.getRiskProfile());
        existingPortfolio.setAdvisorId(updatedPortfolio.getAdvisorId());

        existingPortfolio.setAssets(getAssetsFromPayload(updatedPortfolio));

        return portfolioRepository.save(existingPortfolio);
    }


    public List<AssetHolding> getAssetsFromPayload(Portfolio portfolio) {
        List<AssetHolding> listOfAssets = new ArrayList<>();
        portfolio.getAssets().forEach(asset -> {
            Optional<AssetHolding> optionalAsset = assetRepository.findById(asset.getId());
            AssetHolding foundAsset = optionalAsset.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found with id " + asset.getId()));
            listOfAssets.add(foundAsset);
        });
        return listOfAssets;
    }


}
