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
            @ApiResponse(
                    responseCode = "200",
                    description = "Maintenance request created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Maintenance requests retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public List<MaintenanceRequestResponseDTO> getAllRequests(
            @RequestParam(required = false) MaintenanceRequestStatus status,
            @RequestParam(required = false) String priority) {

        return maintenanceRequestService.getFilteredRequests(
                status,
                priority
        );
    }

    // GET - Get one maintenance request by ID
    @Operation(
            summary = "Get maintenance request by ID",
            description = "Retrieves a specific maintenance request using its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Maintenance request retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request ID"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Maintenance request not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{id}")
    public MaintenanceRequestResponseDTO getRequestById(
            @PathVariable Long id) {

        return maintenanceRequestService.getRequestById(id);
    }

    // PATCH - Update maintenance request status
    @PatchMapping("/{id}/status")
    public MaintenanceRequestResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateMaintenanceRequestStatusDTO dto) {

        return maintenanceRequestService.updateStatus(id, dto.getStatus());
    }
}