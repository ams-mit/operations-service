package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.MaintenanceRequestService;
import com.apartmentsystem.operations.dto.CreateMaintenanceRequestDTO;
import com.apartmentsystem.operations.dto.MaintenanceRequestResponseDTO;
import com.apartmentsystem.operations.dto.UpdateMaintenanceRequestStatusDTO;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/maintenance-requests")
public class MaintenanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    public MaintenanceRequestController(
            MaintenanceRequestService maintenanceRequestService) {

        this.maintenanceRequestService = maintenanceRequestService;
    }

    // POST - Create a maintenance request
    @Operation(
            summary = "Create a maintenance request",
            description = "Creates a new maintenance request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('RESIDENT', 'OWNER')")
    public MaintenanceRequestResponseDTO createRequest(
            @RequestBody CreateMaintenanceRequestDTO dto) {

        return maintenanceRequestService.createRequest(dto);
    }

    // GET - Get all or filtered maintenance requests
    @Operation(
            summary = "Get maintenance requests",
            description = "Retrieves all maintenance requests with optional status and priority filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance requests retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('RESIDENT', 'OWNER', 'COORDINATOR', 'MANAGER')")
    public List<MaintenanceRequestResponseDTO> getAllRequests(
            @RequestParam(required = false) MaintenanceRequestStatus status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Pageable pageable = page == null && size == null
                ? Pageable.unpaged()
                : PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        return maintenanceRequestService.getFilteredRequests(status, priority, category, pageable);
    }

    // GET - Get one maintenance request by ID
    @Operation(
            summary = "Get maintenance request by ID",
            description = "Retrieves a specific maintenance request using its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance request retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Maintenance request not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESIDENT', 'OWNER', 'COORDINATOR', 'MANAGER')")
    public MaintenanceRequestResponseDTO getRequestById(
            @PathVariable Long id) {

        return maintenanceRequestService.getRequestById(id);
    }

    // PATCH - Update maintenance request status
    @Operation(
            summary = "Update maintenance request status",
            description = "Updates the status of a maintenance request according to the allowed status transitions."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance request status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Maintenance request not found"),
            @ApiResponse(responseCode = "409", description = "Invalid maintenance request status transition"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    public MaintenanceRequestResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateMaintenanceRequestStatusDTO dto) {

        return maintenanceRequestService.updateStatus(id, dto);
    }

    // POST - Cancel a maintenance request
    @Operation(
            summary = "Cancel a maintenance request",
            description = "Cancels a maintenance request created by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance request cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to cancel this request"),
            @ApiResponse(responseCode = "404", description = "Maintenance request not found"),
            @ApiResponse(responseCode = "409", description = "Request cannot be cancelled in its current status")
    })
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('RESIDENT', 'OWNER')")
    public MaintenanceRequestResponseDTO cancelRequest(
            @PathVariable Long id) {

        return maintenanceRequestService.cancelRequest(id);
    }
}
