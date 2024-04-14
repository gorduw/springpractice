package com.testing.springpractice.repository;

import com.testing.springpractice.repository.entity.AssetHoldingEntity;
import org.springframework.data.repository.CrudRepository;

public interface AssetRepository extends CrudRepository<AssetHoldingEntity, Long> {
}
