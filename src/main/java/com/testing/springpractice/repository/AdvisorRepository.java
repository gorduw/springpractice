package com.testing.springpractice.repository;

import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface AdvisorRepository extends CrudRepository<AdvisorEntity, Long> {
    List<PortfolioEntity> findPortfoliosById(Long advisorId);
    Optional<AdvisorEntity> findByEmail(String email);
}
