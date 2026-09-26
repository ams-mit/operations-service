package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusHistoryRepository
        extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByMaintenanceRequestIdOrderByChangedAtAsc(
            Long maintenanceRequestId
    );
}