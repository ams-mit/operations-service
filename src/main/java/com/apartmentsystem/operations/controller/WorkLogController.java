package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.WorkLogService;
import com.apartmentsystem.operations.dto.CreateWorkLogDTO;
import com.apartmentsystem.operations.entity.WorkLogEntry;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-orders")
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @PostMapping("/{workOrderId}/logs")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public WorkLogEntry createWorkLog(
            @PathVariable Long workOrderId,
            @RequestBody CreateWorkLogDTO dto) {

        dto.setWorkOrderId(workOrderId);

        return workLogService.createWorkLog(dto);
    }

    @GetMapping("/{workOrderId}/logs")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'COORDINATOR', 'MANAGER')")
    public List<WorkLogEntry> getWorkLogs(
            @PathVariable Long workOrderId) {

        return workLogService.getWorkLogs(workOrderId);
    }
}
