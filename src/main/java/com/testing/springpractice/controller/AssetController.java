package com.testing.springpractice.controller;


import com.testing.springpractice.model.AssetHolding;
import com.testing.springpractice.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/asset")
public class AssetController {
    @Autowired
    private AssetService assetService;


    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createAsset(@RequestBody AssetHolding asset) {
        try {
            AssetHolding newAsset = assetService.createAsset(asset);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAsset);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/data")
    public ResponseEntity getAssetAll() {
        return ResponseEntity.status(HttpStatus.OK).body(assetService.getAssetsAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        try {
            assetService.deleteAsset(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Asset deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting asset");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateAsset(@PathVariable Long id, @RequestBody AssetHolding asset) {
        try {
            AssetHolding updatedAsset = assetService.updateAsset(id, asset);
            return ResponseEntity.ok(updatedAsset);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating asset");
        }
    }
}
