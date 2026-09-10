package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository
        extends JpaRepository<WorkOrder, Long> {
}