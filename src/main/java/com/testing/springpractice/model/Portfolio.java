package com.testing.springpractice.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeRange timeRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskProfile riskProfile;

    @Column(nullable = false, name = "fk_advisor_id")
    private Long advisorId;
/*
    @ManyToOne
    @JoinColumn(name = "advisor_id", nullable = false)
    private Advisor advisor;
*/
    @ManyToMany
    @JoinTable(name = "portfolio_asset_xref",
            joinColumns = @JoinColumn(name = "portfolio_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id"))
    private List<AssetHolding> assets;


    public enum TimeRange {
        LESS_THAN_THREE, THREE_TO_TEN, MORE_THAN_TEN
    }

    public enum RiskProfile {
        S, M, L
    }

}
