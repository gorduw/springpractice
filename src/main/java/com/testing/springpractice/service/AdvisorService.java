package com.testing.springpractice.service;


import com.testing.springpractice.model.Advisor;
import com.testing.springpractice.model.Portfolio;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdvisorService {

    @Autowired
    private AdvisorRepository advisorRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    public List<Portfolio> getAdvisorPortfolios(Long advisorId) {
        Advisor advisor = advisorRepository.findById(advisorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found with id " + advisorId));

        List<Portfolio> portfolios = portfolioRepository.findByAdvisorId(advisorId);

        portfolios.forEach(portfolio -> {
            portfolio.getAssets().size();
        });

        return portfolios;
    }
}
