package com.testing.springpractice.service;


import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.AdvisorToDtoMapperImpl;
import com.testing.springpractice.mapper.PortfolioToDtoMapper;
import com.testing.springpractice.mapper.PortfolioToDtoMapperImpl;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvisorService {

    private final AdvisorRepository advisorRepository;
    private final PortfolioRepository portfolioRepository;

    public AdvisorService(AdvisorRepository advisorRepository, PortfolioRepository portfolioRepository) {
        this.advisorRepository = advisorRepository;
        this.portfolioRepository = portfolioRepository;
    }



    public List<AdvisorDTO> getAllAdvisorDto() {
        List<AdvisorEntity> advisorEntities = new ArrayList<>();
        advisorRepository.findAll().forEach(advisorEntities::add);

        List<AdvisorDTO> advisorDTOs = advisorEntities.stream()
                .map(advisor -> AdvisorToDtoMapperImpl.INSTANCE.advisorToAdvisorDTO(advisor))
                .collect(Collectors.toList());

        return advisorDTOs;
    }

    public AdvisorDTO findAdvisorById(final Long id) {
        return advisorRepository.findById(id)
                .map(AdvisorToDtoMapperImpl.INSTANCE::advisorToAdvisorDTO)
                .orElseThrow(() -> new NotFoundException("Advisor", "ID", id.toString()));
    }

    public AdvisorDTO postAdvisorDto(final AdvisorDTO advisorDTO) {
        AdvisorEntity advisorEntity = advisorRepository.save(AdvisorToDtoMapperImpl.INSTANCE.advisorDtoToAdvisor(advisorDTO));
        return AdvisorToDtoMapperImpl.INSTANCE.advisorToAdvisorDTO(advisorEntity);
    }

    public AdvisorDTO updateAdvisor(final AdvisorDTO advisorDTO) {
        AdvisorEntity advisorEntity = advisorRepository.save(AdvisorToDtoMapperImpl.INSTANCE.advisorDtoToAdvisor(advisorDTO));
        return AdvisorToDtoMapperImpl.INSTANCE.advisorToAdvisorDTO(advisorEntity);
    }
}
