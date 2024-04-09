package com.testing.springpractice.service;

import com.testing.springpractice.model.AssetHolding;
import com.testing.springpractice.model.Portfolio;
import com.testing.springpractice.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<AssetHolding> getAssetsByIds(List<Long> assetIds) {
        List<AssetHolding> assets = new ArrayList<>();
        for (Long assetId : assetIds) {
            AssetHolding asset = assetRepository.findById(assetId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found with id " + assetId));
            assets.add(asset);
        }
        return assets;
    }

    public List<AssetHolding> getAssetsAll() {
        return (List<AssetHolding>) assetRepository.findAll();
    }

    public AssetHolding createAsset(AssetHolding asset) {
        return assetRepository.save(asset);
    }

    public Optional<AssetHolding> findById(Long id) {
        return assetRepository.findById(id);
    }

    public void deleteAsset(Long id) {
        AssetHolding asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id " + id));
        assetRepository.delete(asset);
    }

    public AssetHolding updateAsset(Long id, AssetHolding updatedAsset) {
        AssetHolding existingAsset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id " + id));

        existingAsset.setName(updatedAsset.getName());
        existingAsset.setCode(updatedAsset.getCode());
        existingAsset.setPrice(updatedAsset.getPrice());

        return assetRepository.save(existingAsset);
    }

    public List<Portfolio> getPortfoliosByAssetId(Long assetId) {
        Optional<AssetHolding> asset = assetRepository.findById(assetId);
        if (asset.isPresent()) {
            return asset.get().getPortfolios();
        } else {
            return Collections.emptyList();
        }
    }

}
