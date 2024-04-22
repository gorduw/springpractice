package com.testing.springpractice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioDTO {
    private Long id;
    private String name;
    private String timeRange;
    private String riskProfile;
    private Long advisorId;
    private List<AssetHoldingDTO> assetHoldings;
}
