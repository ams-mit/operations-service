package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.MaintenanceRequestService;
import com.apartmentsystem.operations.dto.CreateMaintenanceRequestDTO;
import com.apartmentsystem.operations.dto.MaintenanceRequestResponseDTO;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public MaintenanceRequestResponseDTO createRequest(
            @RequestBody CreateMaintenanceRequestDTO dto) {

        return maintenanceRequestService.createRequest(dto);
    }

    // GET - Get all or filtered maintenance requests
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
    @GetMapping("/{id}")
    public MaintenanceRequestResponseDTO getRequestById(
            @PathVariable Long id) {

        return maintenanceRequestService.getRequestById(id);
    }
}