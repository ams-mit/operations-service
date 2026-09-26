package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.WorkLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkLogEntryRepository extends JpaRepository<WorkLogEntry, Long> {

    List<WorkLogEntry> findByWorkOrderIdOrderByLoggedAtAsc(Long workOrderId);
}