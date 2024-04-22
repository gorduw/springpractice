package com.testing.springpractice.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssetHoldingDTO(Long id, String name, String code, BigDecimal price, List<PortfolioDTO> portfolioDTOS) {

}
