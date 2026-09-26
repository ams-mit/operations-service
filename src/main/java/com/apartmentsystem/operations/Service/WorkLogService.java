package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.CreateWorkLogDTO;
import com.apartmentsystem.operations.entity.WorkLogEntry;
import com.apartmentsystem.operations.repository.WorkLogEntryRepository;
import com.apartmentsystem.operations.repository.WorkOrderRepository;
import com.apartmentsystem.operations.security.CurrentUser;
import com.apartmentsystem.operations.entity.WorkOrder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkLogService {

    private final WorkLogEntryRepository workLogEntryRepository;
    private final WorkOrderRepository workOrderRepository;

    public WorkLogService(WorkLogEntryRepository workLogEntryRepository,
                          WorkOrderRepository workOrderRepository) {
        this.workLogEntryRepository = workLogEntryRepository;
        this.workOrderRepository = workOrderRepository;
    }

    public WorkLogEntry createWorkLog(CreateWorkLogDTO dto) {

        WorkOrder workOrder = workOrderRepository.findById(dto.getWorkOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work order not found"));
        Long currentUserId = CurrentUser.id();
        if (!currentUserId.equals(workOrder.getAssignedTechnicianUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Technicians may only log work for their assigned work orders");
        }

        WorkLogEntry workLogEntry = new WorkLogEntry();

        workLogEntry.setWorkOrderId(dto.getWorkOrderId());
        workLogEntry.setTechnicianId(currentUserId);
        workLogEntry.setNote(dto.getNote());
        workLogEntry.setLoggedAt(LocalDateTime.now());

        return workLogEntryRepository.save(workLogEntry);
    }

    public List<WorkLogEntry> getWorkLogs(Long workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work order not found"));
        if (!CurrentUser.hasRole("COORDINATOR") && !CurrentUser.hasRole("MANAGER")
                && !CurrentUser.id().equals(workOrder.getAssignedTechnicianUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Technicians may only view logs for their assigned work orders");
        }
        return workLogEntryRepository
                .findByWorkOrderIdOrderByLoggedAtAsc(workOrderId);
    }
}
