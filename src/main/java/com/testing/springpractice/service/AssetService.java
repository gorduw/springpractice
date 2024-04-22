package com.testing.springpractice.service;

import com.testing.springpractice.dto.AssetHoldingDTO;
import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.AdvisorToDtoMapperImpl;
import com.testing.springpractice.mapper.AssetToDtoMapperImpl;
import com.testing.springpractice.repository.AssetRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public List<AssetHoldingDTO> getAssetsDtoAll() {

        List<AssetHoldingDTO> assetHoldingDTOS = new ArrayList<>();
        assetRepository.findAll().forEach(asset -> assetHoldingDTOS.add(AssetToDtoMapperImpl.INSTANCE.assetToAssetDtoWithoutPortfolios(asset)));
        return assetHoldingDTOS;
    }

    public AssetHoldingDTO createAsset(final AssetHoldingDTO asset) {
        AssetHoldingEntity createdAsset = assetRepository.save(AssetToDtoMapperImpl.INSTANCE.assetDtoToAsset(asset));
        return AssetToDtoMapperImpl.INSTANCE.assetToAssetDtoWithoutPortfolios(createdAsset);
    }

    public AssetHoldingEntity findById(final Long id) {
        return assetRepository.findById(id).orElseThrow();
    }

    public void deleteAsset(final Long id) {
        AssetHoldingEntity asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset", "ID", id.toString()));
        assetRepository.delete(asset);
    }

    public AssetHoldingDTO updateAsset(final Long id, final AssetHoldingDTO updatedAsset) {
        AssetHoldingEntity existingAsset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset", "ID", id.toString()));

        existingAsset.setName(updatedAsset.name());
        existingAsset.setCode(updatedAsset.code());
        existingAsset.setPrice(updatedAsset.price());

        AssetHoldingDTO updatedAssetDTO = AssetToDtoMapperImpl.INSTANCE.assetToAssetDtoWithoutPortfolios(assetRepository.save(existingAsset));
        return updatedAssetDTO;
    }

    public List<PortfolioDTO> getPortfoliosByAssetId(final Long assetId) {
        Optional<AssetHoldingEntity> asset = assetRepository.findById(assetId);
        List<PortfolioDTO> portfolioDTOS = new ArrayList<>();
        asset.stream().forEach(a -> System.out.println(AssetToDtoMapperImpl.INSTANCE.assetToAssetDtoWithPortfolios(a).portfolioDTOS()) );
        return portfolioDTOS;
    }

    public BigDecimal getPrice(final Long assetId) {
        System.out.println(assetRepository.findById(assetId));
        return assetRepository.findById(assetId).orElseThrow().getPrice();
    }
}
