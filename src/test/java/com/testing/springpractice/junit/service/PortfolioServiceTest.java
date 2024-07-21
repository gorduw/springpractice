package com.testing.springpractice.junit.service;

import com.testing.springpractice.dto.AssetHoldingDTO;
import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.AssetToDtoMapperImpl;
import com.testing.springpractice.mapper.PortfolioToDtoMapper;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.AssetRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.repository.model.CustomUserDetails;
import com.testing.springpractice.service.AssetService;
import com.testing.springpractice.service.PortfolioService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AdvisorRepository advisorRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Captor
    private ArgumentCaptor<PortfolioEntity> portfolioEntityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Initialize amountLimit for PortfolioService
        portfolioService.setAmountLimit(BigDecimal.valueOf(100));
    }

    @Test
    void testCreatePortfolioWithAssets() {
        PortfolioDTO portfolioDTO = createTestPortfolioDTO();
        AdvisorEntity advisorEntity = createTestAdvisorEntity();
        AssetHoldingEntity assetHoldingEntity = createTestAssetEntity();

        when(advisorRepository.findById(anyLong())).thenReturn(Optional.of(advisorEntity));
        when(assetRepository.findById(anyLong())).thenReturn(Optional.of(assetHoldingEntity));
        when(assetService.getPrice(anyLong())).thenReturn(BigDecimal.TEN);
        when(portfolioRepository.save(any(PortfolioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PortfolioDTO result = portfolioService.createPortfolioWithAssets(portfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioEntityCaptor.capture());
        PortfolioEntity savedEntity = portfolioEntityCaptor.getValue();

        assertNotNull(result);
        assertEquals(portfolioDTO.getName(), savedEntity.getName());
        assertEquals(1, savedEntity.getAssets().size());
    }

    @Test
    void testCreatePortfolioWithAssets_NullDTO() {
        assertThrows(ResponseStatusException.class, () -> portfolioService.createPortfolioWithAssets(null));
    }

    @Test
    void testUpdatePortfolio() {
        Long portfolioId = 1L;
        PortfolioDTO portfolioDTO = createTestPortfolioDTO();
        AdvisorEntity advisorEntity = createTestAdvisorEntity();
        AssetHoldingEntity assetHoldingEntity = createTestAssetEntity();

        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(new PortfolioEntity()));
        when(advisorRepository.findById(anyLong())).thenReturn(Optional.of(advisorEntity));
        when(assetRepository.findById(anyLong())).thenReturn(Optional.of(assetHoldingEntity));
        when(assetService.getPrice(anyLong())).thenReturn(BigDecimal.TEN);
        when(portfolioRepository.save(any(PortfolioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PortfolioDTO result = portfolioService.updatePortfolio(portfolioId, portfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioEntityCaptor.capture());
        PortfolioEntity savedEntity = portfolioEntityCaptor.getValue();

        assertNotNull(result);
        assertEquals(portfolioDTO.getName(), savedEntity.getName());
        assertEquals(1, savedEntity.getAssets().size());
    }

    @Test
    void testUpdatePortfolio_NotFound() {
        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> portfolioService.updatePortfolio(1L, createTestPortfolioDTO()));
    }

    @Test
    void testGetAdvisorPortfolios() {
        Long advisorId = 1L;
        AdvisorEntity advisorEntity = createTestAdvisorEntity();
        PortfolioEntity portfolioEntity = createTestPortfolioEntity();
        CustomUserDetails customUserDetails = createTestCustomUserDetails();

        when(advisorRepository.findById(advisorId)).thenReturn(Optional.of(advisorEntity));
        when(portfolioRepository.findByAdvisorId(advisorId)).thenReturn(Collections.singletonList(portfolioEntity));
        when(authentication.getPrincipal()).thenReturn(customUserDetails);

        List<PortfolioDTO> portfolios = portfolioService.getAdvisorPortfolios(advisorId);

        assertNotNull(portfolios);
        assertEquals(1, portfolios.size());
    }

    @Test
    void testGetAdvisorPortfolios_NotFound() {
        when(advisorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> portfolioService.getAdvisorPortfolios(1L));
    }

    @Test
    void testGetAdvisorPortfolios_AccessDenied() {
        Long advisorId = 1L;
        AdvisorEntity advisorEntity = createTestAdvisorEntity();
        CustomUserDetails customUserDetails = createTestCustomUserDetails();
        customUserDetails.setId(2L);

        when(advisorRepository.findById(advisorId)).thenReturn(Optional.of(advisorEntity));
        when(authentication.getPrincipal()).thenReturn(customUserDetails);

        assertThrows(AccessDeniedException.class, () -> portfolioService.getAdvisorPortfolios(advisorId));
    }

    @Test
    void testValidatePortfolioAmount() {
        List<AssetHoldingEntity> assets = Collections.singletonList(createTestAssetEntity());
        when(assetService.getPrice(anyLong())).thenReturn(BigDecimal.TEN);

        portfolioService.setAmountLimit(BigDecimal.valueOf(100));

        assertDoesNotThrow(() -> portfolioService.validatePortfolioAmount(assets));
    }

    @Test
    void testValidatePortfolioAmount_ExceedsLimit() {
        List<AssetHoldingEntity> assets = Collections.singletonList(createTestAssetEntity());
        when(assetService.getPrice(anyLong())).thenReturn(BigDecimal.valueOf(200));

        portfolioService.setAmountLimit(BigDecimal.valueOf(100));

        assertThrows(BadRequestException.class, () -> portfolioService.validatePortfolioAmount(assets));
    }

    private PortfolioDTO createTestPortfolioDTO() {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setName("Test Portfolio");
        dto.setAdvisorId(1L);
        dto.setAssetHoldings(Collections.singletonList(createTestAssetHoldingDTO()));
        return dto;
    }

    private AdvisorEntity createTestAdvisorEntity() {
        AdvisorEntity entity = new AdvisorEntity();
        entity.setId(1L);
        entity.setName("Test Advisor");
        return entity;
    }

    private AssetHoldingEntity createTestAssetEntity() {
        AssetHoldingEntity entity = new AssetHoldingEntity();
        entity.setId(1L);
        entity.setCode("Test Asset");
        entity.setPrice(BigDecimal.TEN);
        return entity;
    }

    private PortfolioEntity createTestPortfolioEntity() {
        PortfolioEntity entity = new PortfolioEntity();
        entity.setId(1L);
        entity.setName("Test Portfolio");
        entity.setAssets(Collections.singletonList(createTestAssetEntity()));
        return entity;
    }

    private CustomUserDetails createTestCustomUserDetails() {
        return new CustomUserDetails("test@example.com", "password", Collections.emptyList(), 1L, true, null);
    }

    private AssetHoldingDTO createTestAssetHoldingDTO() {
        return new AssetHoldingDTO(1L, "Test Asset", "Test Code", BigDecimal.TEN, null);
    }
}
