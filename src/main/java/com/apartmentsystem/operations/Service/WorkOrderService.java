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

    // Create a work order
    public WorkOrderResponseDTO createWorkOrder(CreateWorkOrderDTO dto) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new RuntimeException("Maintenance request not found"));

        if (maintenanceRequest.getStatus()
                != MaintenanceRequestStatus.SUBMITTED
                && maintenanceRequest.getStatus()
                != MaintenanceRequestStatus.ACKNOWLEDGED) {

            throw new RuntimeException(
                    "Maintenance request must be SUBMITTED or ACKNOWLEDGED before a work order can be created");
        }

        WorkOrder workOrder = new WorkOrder();

        workOrder.setMaintenanceRequestId(
                dto.getMaintenanceRequestId());

        workOrder.setAssignedTechnicianUserId(
                dto.getAssignedTechnicianUserId());

        workOrder.setScheduledDate(
                dto.getScheduledDate());

        // Initial status
        if (dto.getAssignedTechnicianUserId() != null) {
            workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        } else {
            workOrder.setStatus(WorkOrderStatus.CREATED);
        }

        maintenanceRequest.setStatus(MaintenanceRequestStatus.ASSIGNED);
        maintenanceRequestRepository.save(maintenanceRequest);

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
                                new RuntimeException("Work order not found"));

        WorkOrderStatus currentStatus = workOrder.getStatus();

        // Check whether the requested transition is valid
        if (!isValidTransition(currentStatus, newStatus)) {

            throw new RuntimeException(
                    "Invalid work order status transition from "
                            + currentStatus
                            + " to "
                            + newStatus);
        }

        workOrder.setStatus(newStatus);

        if (resolutionNotes != null) {
            workOrder.setResolutionNotes(resolutionNotes);
        }

        WorkOrder updatedWorkOrder =
                workOrderRepository.save(workOrder);

        // When work order is VERIFIED, close the related maintenance request
        if (newStatus == WorkOrderStatus.VERIFIED) {

            MaintenanceRequest request =
                    maintenanceRequestRepository
                            .findById(workOrder.getMaintenanceRequestId())
                            .orElse(null);

            if (request != null) {
                request.setStatus(MaintenanceRequestStatus.CLOSED);
                maintenanceRequestRepository.save(request);
            }
        }

        return convertToResponseDTO(updatedWorkOrder);
    }

    // Validate allowed Work Order status transitions
    private boolean isValidTransition(
            WorkOrderStatus currentStatus,
            WorkOrderStatus newStatus) {

        // CREATED -> ASSIGNED
        if (currentStatus == WorkOrderStatus.CREATED
                && newStatus == WorkOrderStatus.ASSIGNED) {
            return true;
        }

        // ASSIGNED -> IN_PROGRESS
        if (currentStatus == WorkOrderStatus.ASSIGNED
                && newStatus == WorkOrderStatus.IN_PROGRESS) {
            return true;
        }

        // ASSIGNED -> REASSIGNED
        if (currentStatus == WorkOrderStatus.ASSIGNED
                && newStatus == WorkOrderStatus.REASSIGNED) {
            return true;
        }

        // REASSIGNED -> ASSIGNED
        if (currentStatus == WorkOrderStatus.REASSIGNED
                && newStatus == WorkOrderStatus.ASSIGNED) {
            return true;
        }

        // IN_PROGRESS -> COMPLETED
        if (currentStatus == WorkOrderStatus.IN_PROGRESS
                && newStatus == WorkOrderStatus.COMPLETED) {
            return true;
        }

        // COMPLETED -> VERIFIED
        if (currentStatus == WorkOrderStatus.COMPLETED
                && newStatus == WorkOrderStatus.VERIFIED) {
            return true;
        }

        // Any non-terminal status -> CANCELLED
        if (newStatus == WorkOrderStatus.CANCELLED
                && currentStatus != WorkOrderStatus.VERIFIED
                && currentStatus != WorkOrderStatus.CANCELLED) {
            return true;
        }

        return false;
    }

    // Assign or reassign a technician
    public WorkOrderResponseDTO assignTechnician(
            Long orderId,
            Long technicianUserId) {

        WorkOrder workOrder =
                workOrderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException("Work order not found"));

        // Verified work orders cannot be reassigned
        if (workOrder.getStatus() == WorkOrderStatus.VERIFIED
                || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Completed work order cannot be reassigned");
        }

        workOrder.setAssignedTechnicianUserId(technicianUserId);

        WorkOrder updatedWorkOrder =
                workOrderRepository.save(workOrder);

        return convertToResponseDTO(updatedWorkOrder);
    }

    // Convert Entity -> Response DTO
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