package com.testing.springpractice.repository;

import com.testing.springpractice.model.Portfolio;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PortfolioRepository extends CrudRepository<Portfolio, Long> {
    List<Portfolio> findByAdvisorId(Long advisorId);
}
