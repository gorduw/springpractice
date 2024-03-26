package com.testing.springpractice.repository;

import com.testing.springpractice.model.Advisor;
import org.springframework.data.repository.CrudRepository;


public interface AdvisorRepository extends CrudRepository<Advisor, Long> {

}
