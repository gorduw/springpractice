package com.testing.springpractice.mapper;

import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PortfolioToDtoMapper {
    PortfolioToDtoMapper INSTANCE = Mappers.getMapper(PortfolioToDtoMapper.class);

    @Mapping(source = "id", target = "id")
    PortfolioDTO portfolioToPortfolioDTO(PortfolioEntity portfolioEntity);
}
