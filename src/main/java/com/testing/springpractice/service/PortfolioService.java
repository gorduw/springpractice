package com.testing.springpractice.service;

import com.testing.springpractice.dto.AssetHoldingDTO;
import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.exception.ForbiddenException;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.AssetToDtoMapperImpl;
import com.testing.springpractice.mapper.PortfolioToDtoMapper;
import com.testing.springpractice.mapper.PortfolioToDtoMapperImpl;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.AssetRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


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

    public PortfolioDTO createPortfolioWithAssets(final PortfolioDTO portfolioDTO) {
        if (portfolioDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Portfolio is missing");
        }

        AdvisorEntity advisorEntity = advisorRepository.findById(portfolioDTO.getAdvisorId())
                .orElseThrow(() -> new NotFoundException("Advisor", "ID", portfolioDTO.getAdvisorId().toString()));

        PortfolioEntity portfolioEntity = PortfolioToDtoMapper.INSTANCE.portfolioDtoToPortfolio(portfolioDTO);
        portfolioEntity.setAdvisorEntity(advisorEntity);
        List<AssetHoldingEntity> assetHoldingEntities = new ArrayList<>();
        getAssetsFromPayload(portfolioDTO)
                .forEach(assetHoldingDTO -> assetHoldingEntities.add(AssetToDtoMapperImpl.INSTANCE.assetDtoToAsset(assetHoldingDTO)));
        portfolioEntity.setAssets(assetHoldingEntities);

        validatePortfolioAmount(portfolioEntity.getAssets());

        PortfolioEntity savedPortfolio = portfolioRepository.save(portfolioEntity);
        return PortfolioToDtoMapper.INSTANCE.portfolioToPortfolioDTO(savedPortfolio);
    }

    public PortfolioDTO updatePortfolio(final Long id, final PortfolioDTO updatedPortfolio) {
        portfolioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio", "ID", id.toString()));

        AdvisorEntity advisorEntity = advisorRepository.findById(updatedPortfolio.getAdvisorId())
                .orElseThrow(() -> new NotFoundException("Advisor", "ID", updatedPortfolio.getAdvisorId().toString()));

        PortfolioEntity portfolioEntity = PortfolioToDtoMapper.INSTANCE.portfolioDtoToPortfolio(updatedPortfolio);
        portfolioEntity.setAdvisorEntity(advisorEntity);
        List<AssetHoldingEntity> assetHoldingEntities = new ArrayList<>();
        getAssetsFromPayload(updatedPortfolio)
                .forEach(assetHoldingDTO -> assetHoldingEntities.add(AssetToDtoMapperImpl.INSTANCE.assetDtoToAsset(assetHoldingDTO)));
        portfolioEntity.setAssets(assetHoldingEntities);

        validatePortfolioAmount(portfolioEntity.getAssets());

        PortfolioEntity savedPortfolio = portfolioRepository.save(portfolioEntity);
        return PortfolioToDtoMapper.INSTANCE.portfolioToPortfolioDTO(savedPortfolio);
    }


    public List<AssetHoldingDTO> getAssetsFromPayload(final PortfolioDTO portfolioDTO) {
        List<AssetHoldingDTO> listOfAssets = new ArrayList<>();
        portfolioDTO.getAssetHoldings().forEach(asset -> {
            AssetHoldingDTO foundAsset =
                    AssetToDtoMapperImpl.INSTANCE.assetToAssetDtoWithoutPortfolios(
                            assetRepository.findById(asset.id()).orElseThrow(() -> new NotFoundException("Asset", "ID", asset.toString())));
            listOfAssets.add(foundAsset);
        });

        return listOfAssets;
    }

    public List<PortfolioDTO> getAdvisorPortfolios(final Long advisorId) {
        advisorRepository.findById(advisorId)
                .orElseThrow(() -> new NotFoundException("Advisor", "ID", advisorId.toString()));

        List<PortfolioDTO> portfolios = new ArrayList<>();
        portfolioRepository.findByAdvisorId(advisorId)
                .forEach(portfolio -> portfolios.add(PortfolioToDtoMapperImpl.INSTANCE.portfolioToPortfolioDTO(portfolio)));

        portfolios.forEach(portfolio -> {
            portfolio.getAssetHoldings().size();
        });

        return portfolios;
    }


    //TODO remove after next project review
    @Value("${limit.portfolio.amount}")
    BigDecimal amountLimit;

    public void validatePortfolioAmount(final List<AssetHoldingEntity> listOfAssets) {

        AtomicReference<BigDecimal> amount = new AtomicReference<>(BigDecimal.ZERO);

        listOfAssets.forEach(asset -> {
            BigDecimal assetPrice = assetService.getPrice(asset.getId());
            amount.updateAndGet(current -> current.add(assetPrice));
        });

        System.out.println("Limit: " + amountLimit);
        System.out.println("Total amount: " + amount.get());

        if (amount.get().compareTo(amountLimit) > 0) {
            throw new ForbiddenException("Portfolio", amount.toString());
        }
    }


}
