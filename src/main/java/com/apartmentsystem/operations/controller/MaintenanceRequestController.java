package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.dto.CreateMaintenanceRequestDTO;
import com.apartmentsystem.operations.dto.MaintenanceRequestResponseDTO;
import com.apartmentsystem.operations.Service.MaintenanceRequestService;
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

    // GET - Get all maintenance requests
    @GetMapping
    public List<MaintenanceRequestResponseDTO> getAllRequests() {

        return maintenanceRequestService.getAllRequests();
    }

    // GET - Get one maintenance request by ID
    @GetMapping("/{id}")
    public MaintenanceRequestResponseDTO getRequestById(
            @PathVariable Long id) {

        return maintenanceRequestService.getRequestById(id);
    }
}