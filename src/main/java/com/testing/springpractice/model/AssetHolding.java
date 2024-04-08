package com.testing.springpractice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class AssetHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private BigDecimal price;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "portfolio_asset_xref",
            joinColumns = @JoinColumn(name = " asset_id"),
            inverseJoinColumns = @JoinColumn(name = "portfolio_id"))
    private List<Portfolio> portfolios;
}
