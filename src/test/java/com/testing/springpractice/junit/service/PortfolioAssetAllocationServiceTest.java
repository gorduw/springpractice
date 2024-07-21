package com.testing.springpractice.junit.service;

import com.testing.springpractice.dto.PortfolioAssetAllocationDTO;
import com.testing.springpractice.exception.AllocationExceededException;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.PortfolioAssetAllocationMapper;
import com.testing.springpractice.repository.PortfolioAssetAllocationRepository;
import com.testing.springpractice.repository.entity.PortfolioAssetAllocationEntity;
import com.testing.springpractice.service.PortfolioAssetAllocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortfolioAssetAllocationServiceTest {

    @Mock
    private PortfolioAssetAllocationRepository repository;

    @InjectMocks
    private PortfolioAssetAllocationService service;

    @Captor
    private ArgumentCaptor<PortfolioAssetAllocationEntity> entityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAllocations() {
        PortfolioAssetAllocationEntity entity = createTestEntity();
        when(repository.findAll()).thenReturn(Collections.singletonList(entity));

        List<PortfolioAssetAllocationDTO> allocations = service.getAllAllocations();

        assertNotNull(allocations);
        assertEquals(1, allocations.size());
        assertEquals(entity.getAllocationPercentage(), allocations.get(0).getAllocationPercentage());
    }

    @Test
    void testGetAllocation() {
        PortfolioAssetAllocationEntity entity = createTestEntity();
        PortfolioAssetAllocationEntity.PortfolioAssetAllocationId id =
                new PortfolioAssetAllocationEntity.PortfolioAssetAllocationId(1L, 1L);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        PortfolioAssetAllocationDTO allocation = service.getAllocation(1L, 1L);

        assertNotNull(allocation);
        assertEquals(entity.getAllocationPercentage(), allocation.getAllocationPercentage());
    }

    @Test
    void testGetAllocationNotFound() {
        PortfolioAssetAllocationEntity.PortfolioAssetAllocationId id =
                new PortfolioAssetAllocationEntity.PortfolioAssetAllocationId(1L, 1L);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getAllocation(1L, 1L));
    }

    @Test
    void testCreateAllocations() {
        PortfolioAssetAllocationDTO dto = createTestDTO();
        PortfolioAssetAllocationEntity entity = createTestEntity();

        when(repository.findByPortfolioId(1L)).thenReturn(Collections.emptyList());
        when(repository.save(any(PortfolioAssetAllocationEntity.class))).thenReturn(entity);

        List<PortfolioAssetAllocationDTO> createdAllocations = service.createAllocations(Collections.singletonList(dto));

        assertNotNull(createdAllocations);
        assertEquals(1, createdAllocations.size());
        assertEquals(dto.getAllocationPercentage(), createdAllocations.get(0).getAllocationPercentage());
    }

    @Test
    void testCreateAllocationsExceeds() {
        PortfolioAssetAllocationDTO dto1 = createTestDTO();
        PortfolioAssetAllocationDTO dto2 = createTestDTO();
        dto2.setAllocationPercentage(new BigDecimal("90.00"));

        PortfolioAssetAllocationEntity entity = createTestEntity();
        entity.setAllocationPercentage(new BigDecimal("20.00"));

        when(repository.findByPortfolioId(1L)).thenReturn(Collections.singletonList(entity));

        assertThrows(AllocationExceededException.class, () -> service.createAllocations(Arrays.asList(dto1, dto2)));
    }

    @Test
    void testDeleteAllocation() {
        PortfolioAssetAllocationEntity.PortfolioAssetAllocationId id =
                new PortfolioAssetAllocationEntity.PortfolioAssetAllocationId(1L, 1L);
        doNothing().when(repository).deleteById(id);

        service.deleteAllocation(1L, 1L);

        verify(repository, times(1)).deleteById(id);
    }

    private PortfolioAssetAllocationEntity createTestEntity() {
        PortfolioAssetAllocationEntity entity = new PortfolioAssetAllocationEntity();
        entity.setPortfolioId(1L);
        entity.setAssetId(1L);
        entity.setAllocationPercentage(new BigDecimal("50.00"));
        return entity;
    }

    private PortfolioAssetAllocationDTO createTestDTO() {
        return new PortfolioAssetAllocationDTO(1L, 1L, new BigDecimal("50.00"));
    }
}
