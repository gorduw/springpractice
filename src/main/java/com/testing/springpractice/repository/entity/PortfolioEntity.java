package com.testing.springpractice.repository.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.testing.springpractice.repository.enums.RiskProfileEnum;
import com.testing.springpractice.repository.enums.TimeRangeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "portfolio")
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeRangeEnum.TimeRange timeRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskProfileEnum.RiskProfile riskProfile;

    @ManyToOne
    @JoinColumn(name = "fk_advisor_id", nullable = false)
    private AdvisorEntity advisorEntity;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(name = "portfolio_asset_xref",
            joinColumns = @JoinColumn(name = "portfolio_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id"))
    private List<AssetHoldingEntity> assets;

}
