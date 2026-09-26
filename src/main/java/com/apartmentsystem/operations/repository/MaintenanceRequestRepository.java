package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.MaintenanceRequest;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaintenanceRequestRepository
        extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByStatus(MaintenanceRequestStatus status);

    List<MaintenanceRequest> findByPriority(String priority);

    List<MaintenanceRequest> findByStatusAndPriority(
            MaintenanceRequestStatus status,
            String priority
    );

    @Query("select m from MaintenanceRequest m where " +
            "(:status is null or m.status = :status) and " +
            "(:priority is null or m.priority = :priority) and " +
            "(:category is null or m.category = :category)")
    Page<MaintenanceRequest> findFiltered(
            @Param("status") MaintenanceRequestStatus status,
            @Param("priority") String priority,
            @Param("category") String category,
            Pageable pageable);

    @Query("select m.status as label, count(m) as total from MaintenanceRequest m group by m.status")
    List<MaintenanceSummaryProjection> countGroupedByStatus();

    @Query("select m.priority as label, count(m) as total from MaintenanceRequest m group by m.priority")
    List<MaintenanceSummaryProjection> countGroupedByPriority();

    @Query("select m.category as label, count(m) as total from MaintenanceRequest m group by m.category")
    List<MaintenanceSummaryProjection> countGroupedByCategory();
}
