package com.testing.springpractice.dto;

import com.testing.springpractice.repository.entity.PortfolioEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AssetHoldingDTO {
    private Long id;
    private String name;
    private String code;
    private BigDecimal price;
    private List<PortfolioEntity> portfolioEntities;
}
