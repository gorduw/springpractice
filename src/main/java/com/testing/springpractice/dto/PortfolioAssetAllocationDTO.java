package com.testing.springpractice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAssetAllocationDTO {
    private Long portfolioId;
    private Long assetId;
    private BigDecimal allocationPercentage;
    //When its good to have these fields in dto?
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long createdBy;

    public PortfolioAssetAllocationDTO(Long portfolioId, Long assetId, BigDecimal allocationPercentage) {
        this.portfolioId = portfolioId;
        this.assetId = assetId;
        this.allocationPercentage = allocationPercentage;
    }
}
