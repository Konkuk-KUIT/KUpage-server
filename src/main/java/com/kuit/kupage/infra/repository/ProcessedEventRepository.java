package com.kuit.kupage.infra.repository;

import com.kuit.kupage.infra.domain.ProcessedEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventLog, Long> {
}