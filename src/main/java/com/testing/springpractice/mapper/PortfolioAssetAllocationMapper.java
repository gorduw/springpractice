package com.testing.springpractice.mapper;

import com.testing.springpractice.dto.PortfolioAssetAllocationDTO;
import com.testing.springpractice.repository.entity.PortfolioAssetAllocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PortfolioAssetAllocationMapper {
    PortfolioAssetAllocationMapper INSTANCE = Mappers.getMapper(PortfolioAssetAllocationMapper.class);

    PortfolioAssetAllocationDTO entityToDto(PortfolioAssetAllocationEntity entity);

    PortfolioAssetAllocationEntity dtoToEntity(PortfolioAssetAllocationDTO dto);
}
