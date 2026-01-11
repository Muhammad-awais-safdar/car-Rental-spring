package com.marketplace.category.repository;

import com.marketplace.category.entity.Make;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MakeRepository extends JpaRepository<Make, Long> {
    Optional<Make> findByName(String name);

    boolean existsByName(String name);
}
