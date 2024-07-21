package com.testing.springpractice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.testing.springpractice.dto.AssetHoldingDTO;
import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.AssetToDtoMapperImpl;
import com.testing.springpractice.repository.AssetRepository;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final WebClient webClient;
    private final CacheManager cacheManager;
    private final List<String> predefinedStockCodes;
    private final String apiUrl;
    private final String apiKey;

    public AssetService(AssetRepository assetRepository, WebClient webClient, CacheManager cacheManager,
                        @Value("#{'${stock.codes}'.split(',')}") List<String> predefinedStockCodes,
                        @Value("${stock.api.url}") String apiUrl,
                        @Value("${stock.api.api-key}") String apiKey) {
        this.assetRepository = assetRepository;
        this.webClient = webClient;
        this.cacheManager = cacheManager;
        this.predefinedStockCodes = predefinedStockCodes;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
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

    @Cacheable("stockList")
    public Flux<AssetHoldingEntity> getStockList() {
        return Flux.fromIterable(predefinedStockCodes)
                .flatMap(this::fetchAndMapStockData)
                .doOnNext(stock -> System.out.println("Fetched stock: " + stock))
                .doOnError(e -> System.err.println("Error fetching stock list: " + e.getMessage()));
    }

    @Cacheable("assets")
    public Mono<AssetHoldingEntity> getStockByCode(String code) {
        return fetchAndMapStockData(code)
                .doOnNext(stock -> System.out.println("Fetched stock for code " + code + ": " + stock))
                .doOnError(e -> System.err.println("Error fetching stock by code: " + code + ": " + e.getMessage()));
    }

    private Mono<AssetHoldingEntity> fetchAndMapStockData(String code) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(apiUrl)
                        .path("/query")
                        .queryParam("function", "TIME_SERIES_DAILY")
                        .queryParam("symbol", code)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("Received response for code " + code + ": " + response))
                .flatMap(response -> {
                    try {
                        return Mono.just(parseAndMapToEntity(code, response));
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException("Error parsing response for stock code: " + code, e));
                    }
                })
                .doOnError(e -> System.err.println("Error fetching stock data for code " + code + ": " + e.getMessage()));
    }

    private AssetHoldingEntity parseAndMapToEntity(String code, String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode timeSeriesNode = root.path("Time Series (Daily)");

        Iterator<Map.Entry<String, JsonNode>> fields = timeSeriesNode.fields();
        if (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode latestData = entry.getValue();

            BigDecimal price = new BigDecimal(latestData.path("4. close").asText());

            AssetHoldingEntity entity = new AssetHoldingEntity();
            entity.setName(code);
            entity.setCode(code);
            entity.setPrice(price);

            return entity;
        } else {
            throw new IOException("No data available for stock code: " + code + ". Response: " + jsonResponse);
        }
    }

}
