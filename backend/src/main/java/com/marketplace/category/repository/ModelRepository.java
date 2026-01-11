package com.marketplace.category.repository;

import com.marketplace.category.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    List<Model> findByMakeId(Long makeId);

    Optional<Model> findByMakeIdAndName(Long makeId, String name);

    boolean existsByMakeIdAndName(Long makeId, String name);
}
