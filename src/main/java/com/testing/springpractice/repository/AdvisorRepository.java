package com.testing.springpractice.repository;

import com.testing.springpractice.model.Advisor;
import com.testing.springpractice.model.Portfolio;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface AdvisorRepository extends CrudRepository<Advisor, Long> {
    List<Portfolio> findPortfoliosById(Long advisorId);
}
