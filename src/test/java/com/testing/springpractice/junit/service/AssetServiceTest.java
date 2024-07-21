package com.testing.springpractice.junit.service;

import com.testing.springpractice.dto.AssetHoldingDTO;
import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.AssetToDtoMapperImpl;
import com.testing.springpractice.repository.AssetRepository;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.service.AssetService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    @Mock
    private CacheManager cacheManager;

    private MockWebServer mockWebServer;

    @Mock
    private WebClient webClient;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().build();

        List<String> predefinedStockCodes = Arrays.asList("AAPL", "GOOGL");
        String apiKey = "dummy-api-key"; // Use a dummy API key for testing

        assetService = new AssetService(assetRepository, webClient, cacheManager, predefinedStockCodes, baseUrl, apiKey);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetStockList() {
        String mockResponse = "{ \"Time Series (Daily)\": { \"2023-07-17\": { \"4. close\": \"150.00\" } } }";
        System.out.println(mockWebServer.getHostName());
        mockWebServer.enqueue(new MockResponse().setBody(mockResponse).addHeader("Content-Type", "application/json"));

        Flux<AssetHoldingEntity> stockList = assetService.getStockList();
        stockList.doOnNext(System.out::println);
    }

    @Test
    void testGetStockByCode() {
        String stockCode = "AAPL";
        String mockResponse = "{ \"Time Series (Daily)\": { \"2023-07-17\": { \"4. close\": \"150.00\" } } }";
        mockWebServer.enqueue(new MockResponse().setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
                .addHeader("Host", ""));

        Mono<AssetHoldingEntity> stock = assetService.getStockByCode(stockCode);

        System.out.println(stock);
    }

    @Test
    void testGetAssetsDtoAll() {
        AssetHoldingEntity asset = createTestAssetEntity();
        when(assetRepository.findAll()).thenReturn(Collections.singletonList(asset));

        List<AssetHoldingDTO> assetHoldingDTOs = assetService.getAssetsDtoAll();

        assertNotNull(assetHoldingDTOs);
        assertEquals(1, assetHoldingDTOs.size());
        assertEquals(asset.getName(), assetHoldingDTOs.get(0).name());
    }

    @Test
    void testCreateAsset() {
        AssetHoldingDTO assetDTO = createTestAssetDTO();
        AssetHoldingEntity assetEntity = createTestAssetEntity();

        when(assetRepository.save(any(AssetHoldingEntity.class))).thenReturn(assetEntity);

        AssetHoldingDTO createdAsset = assetService.createAsset(assetDTO);

        assertNotNull(createdAsset);
        assertEquals(assetDTO.name(), createdAsset.name());
    }

    @Test
    void testFindById() {
        AssetHoldingEntity asset = createTestAssetEntity();
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.of(asset));

        AssetHoldingEntity foundAsset = assetService.findById(1L);

        assertNotNull(foundAsset);
        assertEquals(asset.getName(), foundAsset.getName());
    }

    @Test
    void testFindByIdNotFound() {
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> assetService.findById(1L));
    }

    @Test
    void testDeleteAsset() {
        AssetHoldingEntity asset = createTestAssetEntity();
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.of(asset));
        doNothing().when(assetRepository).delete(any(AssetHoldingEntity.class));

        assetService.deleteAsset(1L);

        verify(assetRepository, times(1)).delete(asset);
    }

    @Test
    void testDeleteAssetNotFound() {
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> assetService.deleteAsset(1L));
    }

    @Test
    void testUpdateAsset() {
        AssetHoldingEntity asset = createTestAssetEntity();
        AssetHoldingDTO assetDTO = createTestAssetDTO();

        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(AssetHoldingEntity.class))).thenReturn(asset);

        AssetHoldingDTO updatedAsset = assetService.updateAsset(1L, assetDTO);

        assertNotNull(updatedAsset);
        assertEquals(assetDTO.name(), updatedAsset.name());
    }

    @Test
    void testUpdateAssetNotFound() {
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> assetService.updateAsset(1L, createTestAssetDTO()));
    }

    @Test
    void testGetPortfoliosByAssetId() {
        AssetHoldingEntity asset = createTestAssetEntity();
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.of(asset));

        List<PortfolioDTO> portfolioDTOS = assetService.getPortfoliosByAssetId(1L);

        assertNotNull(portfolioDTOS);
        // Add more assertions based on actual behavior and mapping
    }

    @Test
    void testGetPrice() {
        AssetHoldingEntity asset = createTestAssetEntity();
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.of(asset));

        BigDecimal price = assetService.getPrice(1L);

        assertNotNull(price);
        assertEquals(asset.getPrice(), price);
    }

    @Test
    void testGetPriceNotFound() {
        when(assetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> assetService.getPrice(1L));
    }


    private AssetHoldingEntity createTestAssetEntity() {
        AssetHoldingEntity asset = new AssetHoldingEntity();
        asset.setId(1L);
        asset.setName("Test Asset");
        asset.setCode("TA");
        asset.setPrice(BigDecimal.valueOf(100.00));
        return asset;
    }

    private AssetHoldingDTO createTestAssetDTO() {
        return new AssetHoldingDTO(1L, "Test Asset", "TA", BigDecimal.valueOf(100.00), Collections.emptyList());
    }
}
