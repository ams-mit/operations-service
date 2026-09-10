package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.MaintenanceRequest;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository
        extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByStatus(MaintenanceRequestStatus status);

    List<MaintenanceRequest> findByPriority(String priority);

    List<MaintenanceRequest> findByStatusAndPriority(
            MaintenanceRequestStatus status,
            String priority
    );
}