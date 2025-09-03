package com.betvictor.repository.repository;

import com.betvictor.repository.model.ProcessingResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingResultRepository extends JpaRepository<ProcessingResultEntity, Long> {
    List<ProcessingResultEntity> findTop10ByOrderByCreatedAtDesc();
}