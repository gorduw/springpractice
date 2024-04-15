package com.testing.springpractice.mapper;


import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdvisorToDtoMapper {
AdvisorToDtoMapper INSTANCE = Mappers.getMapper(AdvisorToDtoMapper.class);

    @Mapping(source = "id", target = "advisorId")
    AdvisorDTO advisorToAdvisorDTO(AdvisorEntity advisorEntity);

    AdvisorEntity advisorDtoToAdvisor(AdvisorDTO advisorDTO);
}
