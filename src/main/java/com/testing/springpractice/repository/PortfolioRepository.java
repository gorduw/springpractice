package com.testing.springpractice.repository;

import com.testing.springpractice.repository.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PortfolioRepository extends CrudRepository<PortfolioEntity, Long> {
    @Query("SELECT p FROM PortfolioEntity p WHERE p.advisorEntity.id = :advisorId")
    List<PortfolioEntity> findByAdvisorId(Long advisorId);
}
