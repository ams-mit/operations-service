package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.CreateMaintenanceRequestDTO;
import com.apartmentsystem.operations.dto.MaintenanceRequestResponseDTO;
import com.apartmentsystem.operations.dto.UpdateMaintenanceRequestStatusDTO;
import com.apartmentsystem.operations.entity.MaintenanceRequest;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import com.apartmentsystem.operations.entity.StatusHistory;
import com.apartmentsystem.operations.exception.InvalidStatusTransitionException;
import com.apartmentsystem.operations.repository.MaintenanceRequestRepository;
import com.apartmentsystem.operations.repository.StatusHistoryRepository;
import com.apartmentsystem.operations.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    public MaintenanceRequestService(
            MaintenanceRequestRepository maintenanceRequestRepository,
            StatusHistoryRepository statusHistoryRepository) {

        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    // Create a maintenance request
    public MaintenanceRequestResponseDTO createRequest(
            CreateMaintenanceRequestDTO dto) {

        MaintenanceRequest request = new MaintenanceRequest();

        request.setUnitId(dto.getUnitId());
        request.setCategory(dto.getCategory());
        request.setPriority(dto.getPriority());
        request.setDescription(dto.getDescription());
        request.setAttachmentUrl(dto.getAttachmentUrl());

        // Get the currently authenticated user's ID
        Long currentUserId = CurrentUser.id();
        request.setRequestedByUserId(currentUserId);

        // System-controlled fields
        request.setStatus(MaintenanceRequestStatus.SUBMITTED);

        LocalDateTime now = LocalDateTime.now();
        request.setCreatedAt(now);
        request.setUpdatedAt(now);

        // Save to database
        MaintenanceRequest savedRequest =
                maintenanceRequestRepository.save(request);

        // Convert Entity → Response DTO
        return convertToResponseDTO(savedRequest);
    }

    // Get all maintenance requests
    public List<MaintenanceRequestResponseDTO> getAllRequests() {
        return getFilteredRequests(null, null, null, Pageable.unpaged());
    }

    // Get one maintenance request by ID
    public MaintenanceRequestResponseDTO getRequestById(Long id) {

        MaintenanceRequest request =
                maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Maintenance request not found"));

        if (CurrentUser.hasAnyRole("RESIDENT", "OWNER")
                && !CurrentUser.id().equals(request.getRequestedByUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to access this maintenance request");
        }

        return convertToResponseDTO(request);
    }

    // Filter maintenance requests by status and/or priority
    public List<MaintenanceRequestResponseDTO> getFilteredRequests(
            MaintenanceRequestStatus status,
            String priority) {
        return getFilteredRequests(status, priority, null, Pageable.unpaged());
    }

    public List<MaintenanceRequestResponseDTO> getFilteredRequests(
            MaintenanceRequestStatus status, String priority, String category,
            Pageable pageable) {
        Long requesterId = CurrentUser.hasAnyRole("RESIDENT", "OWNER") ? CurrentUser.id() : null;
        return maintenanceRequestRepository.findFiltered(status, priority, category, requesterId, pageable)
                .map(this::convertToResponseDTO).toList();
    }

    public List<MaintenanceRequestResponseDTO> getAllRequests(Pageable pageable) {
        return getFilteredRequests(null, null, null, pageable);
    }

    // Update maintenance request status
    public MaintenanceRequestResponseDTO updateStatus(
            Long id,
            UpdateMaintenanceRequestStatusDTO dto) {

        MaintenanceRequest request =
                maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Maintenance request not found"));

        MaintenanceRequestStatus currentStatus = request.getStatus();
        MaintenanceRequestStatus newStatus = dto.getStatus();

        // Check whether the status transition is allowed
        if (!isValidTransition(currentStatus, newStatus)) {

            throw new InvalidStatusTransitionException(
                    "Invalid maintenance request status transition: "
                            + currentStatus + " -> " + newStatus);
        }

        // Update status
        request.setStatus(newStatus);
        request.setUpdatedAt(LocalDateTime.now());

        // Save updated maintenance request
        MaintenanceRequest updatedRequest =
                maintenanceRequestRepository.save(request);

        // Create status history record
        StatusHistory history = new StatusHistory();

        history.setMaintenanceRequestId(request.getId());
        history.setOldStatus(currentStatus);
        history.setNewStatus(newStatus);
        history.setChangedByUserId(CurrentUser.id());
        history.setChangedAt(LocalDateTime.now());

        statusHistoryRepository.save(history);

        return convertToResponseDTO(updatedRequest);
    }

    // Cancel a maintenance request
    public MaintenanceRequestResponseDTO cancelRequest(Long id) {

        MaintenanceRequest request =
                maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Maintenance request not found"));

        // Get the currently authenticated user
        Long currentUserId = CurrentUser.id();

        // Only the person who created the request can cancel it
        if (!currentUserId.equals(request.getRequestedByUserId())) {

            throw new RuntimeException(
                    "Only the user who created the maintenance request can cancel it");
        }

        // Cancellation is allowed only from SUBMITTED or ACKNOWLEDGED
        if (request.getStatus() != MaintenanceRequestStatus.SUBMITTED
                && request.getStatus() != MaintenanceRequestStatus.ACKNOWLEDGED) {

            throw new InvalidStatusTransitionException(
                    "Maintenance request cannot be cancelled from status: "
                            + request.getStatus());
        }

        // Store old status before changing it
        MaintenanceRequestStatus oldStatus = request.getStatus();

        // Change status to CANCELLED
        request.setStatus(MaintenanceRequestStatus.CANCELLED);
        request.setUpdatedAt(LocalDateTime.now());

        // Save cancelled request
        MaintenanceRequest cancelledRequest =
                maintenanceRequestRepository.save(request);

        // Create status history record
        StatusHistory history = new StatusHistory();

        history.setMaintenanceRequestId(request.getId());
        history.setOldStatus(oldStatus);
        history.setNewStatus(MaintenanceRequestStatus.CANCELLED);
        history.setChangedByUserId(currentUserId);
        history.setChangedAt(LocalDateTime.now());

        statusHistoryRepository.save(history);

        return convertToResponseDTO(cancelledRequest);
    }

    // Get status history
    public List<StatusHistory> getStatusHistory(
            Long maintenanceRequestId) {

        // Make sure the maintenance request exists
        if (!maintenanceRequestRepository.existsById(maintenanceRequestId)) {

            throw new RuntimeException(
                    "Maintenance request not found");
        }

        return statusHistoryRepository
                .findByMaintenanceRequestIdOrderByChangedAtAsc(
                        maintenanceRequestId
                );
    }

    // Check whether a status transition is allowed
    private boolean isValidTransition(
            MaintenanceRequestStatus currentStatus,
            MaintenanceRequestStatus newStatus) {

        return switch (currentStatus) {

            case SUBMITTED ->
                    newStatus == MaintenanceRequestStatus.ACKNOWLEDGED
                            || newStatus == MaintenanceRequestStatus.REJECTED
                            || newStatus == MaintenanceRequestStatus.CANCELLED;

            case ACKNOWLEDGED ->
                    newStatus == MaintenanceRequestStatus.ASSIGNED
                            || newStatus == MaintenanceRequestStatus.CANCELLED;

            case ASSIGNED ->
                    newStatus == MaintenanceRequestStatus.IN_PROGRESS;

            case IN_PROGRESS ->
                    newStatus == MaintenanceRequestStatus.RESOLVED;

            case RESOLVED ->
                    newStatus == MaintenanceRequestStatus.CLOSED;

            case CLOSED, REJECTED, CANCELLED ->
                    false;
        };
    }

    // Convert Entity → Response DTO
    private MaintenanceRequestResponseDTO convertToResponseDTO(
            MaintenanceRequest request) {

        MaintenanceRequestResponseDTO response =
                new MaintenanceRequestResponseDTO();

        response.setId(request.getId());
        response.setUnitId(request.getUnitId());
        response.setRequestedByUserId(request.getRequestedByUserId());
        response.setStatus(request.getStatus());
        response.setCategory(request.getCategory());
        response.setPriority(request.getPriority());
        response.setDescription(request.getDescription());
        response.setAttachmentUrl(request.getAttachmentUrl());
        response.setCreatedAt(request.getCreatedAt());
        response.setUpdatedAt(request.getUpdatedAt());

        return response;
    }

    // Update maintenance request status
    public MaintenanceRequestResponseDTO updateStatus(
            Long id,
            MaintenanceRequestStatus newStatus) {

        MaintenanceRequest request =
                maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Maintenance request not found"));

        request.setStatus(newStatus);
        
        MaintenanceRequest updatedRequest =
                maintenanceRequestRepository.save(request);

        return convertToResponseDTO(updatedRequest);
    }
}
