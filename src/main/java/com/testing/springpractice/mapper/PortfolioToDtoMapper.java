package com.testing.springpractice.mapper;

import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PortfolioToDtoMapper {
    PortfolioToDtoMapper INSTANCE = Mappers.getMapper(PortfolioToDtoMapper.class);

    @Mappings({
            @Mapping(source = "advisorEntity.id", target = "advisorId"),
            @Mapping(source = "assets", target = "assetHoldings")
    })
    PortfolioDTO portfolioToPortfolioDTO(PortfolioEntity portfolioEntity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "advisorEntity", ignore = true) // Ignore the advisorEntity mapping here
    })
    PortfolioEntity portfolioDtoToPortfolio(PortfolioDTO portfolioDTO);
}
