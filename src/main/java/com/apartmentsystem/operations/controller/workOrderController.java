package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.WorkOrderService;
import com.apartmentsystem.operations.dto.CreateWorkOrderDTO;
import com.apartmentsystem.operations.dto.WorkOrderResponseDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/work-orders")
public class workOrderController {

    private final WorkOrderService workOrderService;

    public workOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    // POST - Create and assign a work order
    @PostMapping
    public WorkOrderResponseDTO createWorkOrder(
            @RequestBody CreateWorkOrderDTO dto) {

        return workOrderService.createWorkOrder(dto);
    }
}