package com.testing.springpractice.controller;


import com.testing.springpractice.dto.AssetHoldingDTO;
import com.testing.springpractice.service.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public ResponseEntity createAsset(@RequestBody AssetHoldingDTO asset) {
        AssetHoldingDTO newAsset = assetService.createAsset(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAsset);
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity getAssetAll() {
        return ResponseEntity.status(HttpStatus.OK).body(assetService.getAssetsDtoAll());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity getPortfolioAssignedToAsset(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(assetService.getPortfoliosByAssetId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAsset(@PathVariable final Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Asset deleted successfully");
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateAsset(@PathVariable Long id, @RequestBody AssetHoldingDTO asset) {
        AssetHoldingDTO updatedAsset = assetService.updateAsset(id, asset);
        return ResponseEntity.ok(updatedAsset);
    }
}
