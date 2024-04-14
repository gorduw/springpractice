package com.testing.springpractice.service;


import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.mapper.AdvisorToDtoMapper;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdvisorService {

    private final AdvisorRepository advisorRepository;
    private final PortfolioRepository portfolioRepository;

    public AdvisorService(AdvisorRepository advisorRepository, PortfolioRepository portfolioRepository) {
        this.advisorRepository = advisorRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public List<PortfolioEntity> getAdvisorPortfolios(final Long advisorId) {
        AdvisorEntity advisorEntity = advisorRepository.findById(advisorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found with id " + advisorId));

        List<PortfolioEntity> portfolioEntities = portfolioRepository.findByAdvisorId(advisorId);

        portfolioEntities.forEach(portfolio -> {
            portfolio.getAssets().size();
        });

        return portfolioEntities;
    }

    public AdvisorDTO getAdvisorDto(Long id) {
        return AdvisorToDtoMapper.INSTANCE.advisorToAdvisorDTO(advisorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found with id " + id)));
    }
}
