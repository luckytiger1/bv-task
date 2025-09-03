package com.betvictor.repository.service;

import com.betvictor.repository.model.ProcessingResultEntity;
import com.betvictor.repository.repository.ProcessingResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcessingResultService {
    private final ProcessingResultRepository repository;

    public ProcessingResultService(ProcessingResultRepository repository) {
        this.repository = repository;
    }

    public void save(ProcessingResultEntity entity) {
        repository.save(entity);
    }

    public List<ProcessingResultEntity> getLastTenResults() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }
}