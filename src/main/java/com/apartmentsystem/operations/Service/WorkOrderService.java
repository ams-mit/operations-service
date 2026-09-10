package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.CreateWorkOrderDTO;
import com.apartmentsystem.operations.dto.WorkOrderResponseDTO;
import com.apartmentsystem.operations.entity.MaintenanceRequest;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import com.apartmentsystem.operations.entity.WorkOrder;
import com.apartmentsystem.operations.entity.WorkOrderStatus;
import com.apartmentsystem.operations.repository.MaintenanceRequestRepository;
import com.apartmentsystem.operations.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            MaintenanceRequestRepository maintenanceRequestRepository) {

        this.workOrderRepository = workOrderRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    // Create and assign a work order
    public WorkOrderResponseDTO createWorkOrder(CreateWorkOrderDTO dto) {

        // Find the maintenance request
        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Maintenance request not found"));

        // A maintenance request must be SUBMITTED before it can be assigned
        if (maintenanceRequest.getStatus()
                != MaintenanceRequestStatus.SUBMITTED) {

            throw new RuntimeException(
                    "Maintenance request must be SUBMITTED before it can be assigned");
        }

        // Create WorkOrder
        WorkOrder workOrder = new WorkOrder();

        workOrder.setMaintenanceRequestId(
                dto.getMaintenanceRequestId());

        workOrder.setAssignedTechnicianUserId(
                dto.getAssignedTechnicianUserId());

        workOrder.setScheduledDate(
                dto.getScheduledDate());

        // Initial status
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);

        // Save WorkOrder
        WorkOrder savedWorkOrder =
                workOrderRepository.save(workOrder);

        return convertToResponseDTO(savedWorkOrder);
    }

    // Get all work orders
    public List<WorkOrderResponseDTO> getAllWorkOrders() {

        return workOrderRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // Update work order status
    public WorkOrderResponseDTO updateStatus(
            Long orderId,
            WorkOrderStatus newStatus,
            String resolutionNotes) {

        WorkOrder workOrder =
                workOrderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Work order not found"));

        // A closed work order cannot be reopened
        if (workOrder.getStatus() == WorkOrderStatus.CLOSED) {

            throw new RuntimeException(
                    "Closed work order cannot be reopened or reassigned");
        }

        workOrder.setStatus(newStatus);

        if (resolutionNotes != null) {
            workOrder.setResolutionNotes(resolutionNotes);
        }

        WorkOrder updatedWorkOrder =
                workOrderRepository.save(workOrder);

        return convertToResponseDTO(updatedWorkOrder);
    }

    // Convert Entity → Response DTO
    private WorkOrderResponseDTO convertToResponseDTO(
            WorkOrder workOrder) {

        WorkOrderResponseDTO response =
                new WorkOrderResponseDTO();

        response.setId(workOrder.getId());

        response.setMaintenanceRequestId(
                workOrder.getMaintenanceRequestId());

        response.setAssignedTechnicianUserId(
                workOrder.getAssignedTechnicianUserId());

        response.setScheduledDate(
                workOrder.getScheduledDate());

        response.setStatus(
                workOrder.getStatus());

        response.setResolutionNotes(
                workOrder.getResolutionNotes());

        return response;
    }
}
