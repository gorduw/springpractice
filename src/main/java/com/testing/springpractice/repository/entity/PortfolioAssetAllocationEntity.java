package com.testing.springpractice.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "portfolio_asset_allocation")
@IdClass(PortfolioAssetAllocationEntity.PortfolioAssetAllocationId.class)
public class PortfolioAssetAllocationEntity extends Auditable {

    @Id
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Id
    @Column(name = "asset_id")
    private Long assetId;

    @Column(nullable = false)
    private BigDecimal allocationPercentage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PortfolioAssetAllocationId implements Serializable {
        private Long portfolioId;
        private Long assetId;
    }
}
