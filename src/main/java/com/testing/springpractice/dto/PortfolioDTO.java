package com.testing.springpractice.dto;


import lombok.Data;

import java.util.List;

@Data
public class PortfolioDTO {
    private Long id;
    private String name;
    private String timeRange;
    private String riskProfile;
    private AdvisorDTO advisorDTO;
    private List<Long> assetIds;
}
