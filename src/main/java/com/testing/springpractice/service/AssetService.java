package com.testing.springpractice.service;

import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public List<AssetHoldingEntity> getAssetsAll() {
        return (List<AssetHoldingEntity>) assetRepository.findAll();
    }

    public AssetHoldingEntity createAsset(final AssetHoldingEntity asset) {
        return assetRepository.save(asset);
    }

    public Optional<AssetHoldingEntity> findById(final Long id) {
        return assetRepository.findById(id);
    }

    public void deleteAsset(final Long id) {
        AssetHoldingEntity asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset", "ID", id.toString()));
        assetRepository.delete(asset);
    }

    public AssetHoldingEntity updateAsset(final Long id, final AssetHoldingEntity updatedAsset) {
        AssetHoldingEntity existingAsset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset", "ID", id.toString()));

        existingAsset.setName(updatedAsset.getName());
        existingAsset.setCode(updatedAsset.getCode());
        existingAsset.setPrice(updatedAsset.getPrice());

        return assetRepository.save(existingAsset);
    }

    public List<PortfolioEntity> getPortfoliosByAssetId(final Long assetId) {
        Optional<AssetHoldingEntity> asset = assetRepository.findById(assetId);
        if (asset.isPresent()) {
            return asset.get().getPortfolioEntities();
        } else {
            return Collections.emptyList();
        }
    }
}
