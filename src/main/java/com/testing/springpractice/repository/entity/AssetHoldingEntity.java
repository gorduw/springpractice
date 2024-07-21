package com.testing.springpractice.repository.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "asset_holding")
public class AssetHoldingEntity extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private BigDecimal price;

    @JsonBackReference
    @ManyToMany
    @JoinTable(name = "portfolio_asset_xref", joinColumns = @JoinColumn(name = "asset_id"), inverseJoinColumns = @JoinColumn(name = "portfolio_id"))
    private List<PortfolioEntity> portfolioEntities;

    @Override
    public String toString() {
        return "AssetHolding{" + "id=" + id + ", name='" + name + '\'' + ", code='" + code + '\'' + ", price=" + price + '}';
    }
}
