package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.apartmentsystem.operations.entity.WorkOrderStatus;

public interface WorkOrderRepository
        extends JpaRepository<WorkOrder, Long> {

    @Query("select w from WorkOrder w where " +
            "(:technicianId is null or w.assignedTechnicianUserId = :technicianId) and " +
            "(:status is null or w.status = :status)")
    Page<WorkOrder> findFiltered(@Param("technicianId") Long technicianId,
                                 @Param("status") WorkOrderStatus status,
                                 Pageable pageable);
}
