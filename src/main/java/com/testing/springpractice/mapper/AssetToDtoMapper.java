package com.testing.springpractice.mapper;

import com.testing.springpractice.dto.AssetHoldingDTO;
import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AssetToDtoMapper {
    AssetToDtoMapper INSTANCE = Mappers.getMapper(AssetToDtoMapper.class);

    @Mapping(target = "portfolioDTOS", ignore = true)
    AssetHoldingDTO assetToAssetDtoWithoutPortfolios(AssetHoldingEntity assetHoldingEntity);

    AssetHoldingDTO assetToAssetDtoWithPortfolios(AssetHoldingEntity assetHoldingEntity);

    AssetHoldingEntity assetDtoToAsset(AssetHoldingDTO assetHoldingDTO);
}
