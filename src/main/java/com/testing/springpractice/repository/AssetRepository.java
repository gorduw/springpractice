package com.testing.springpractice.repository;

import com.testing.springpractice.model.AssetHolding;
import org.springframework.data.repository.CrudRepository;

public interface AssetRepository extends CrudRepository<AssetHolding, Long> {
}
