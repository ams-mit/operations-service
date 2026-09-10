package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.CreateMaintenanceRequestDTO;
import com.apartmentsystem.operations.dto.MaintenanceRequestResponseDTO;
import com.apartmentsystem.operations.entity.MaintenanceRequest;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import com.apartmentsystem.operations.repository.MaintenanceRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequestService(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    // Create a maintenance request
    public MaintenanceRequestResponseDTO createRequest(CreateMaintenanceRequestDTO dto) {

        MaintenanceRequest request = new MaintenanceRequest();

        request.setUnitId(dto.getUnitId());
        request.setCategory(dto.getCategory());
        request.setPriority(dto.getPriority());
        request.setDescription(dto.getDescription());
        request.setAttachmentUrl(dto.getAttachmentUrl());

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

        return maintenanceRequestRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // Get one maintenance request by ID
    public MaintenanceRequestResponseDTO getRequestById(Long id) {

        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Maintenance request not found"));

        return convertToResponseDTO(request);
    }

    // Convert Entity → Response DTO
    private MaintenanceRequestResponseDTO convertToResponseDTO(
            MaintenanceRequest request) {

        MaintenanceRequestResponseDTO response =
                new MaintenanceRequestResponseDTO();

        response.setId(request.getId());
        response.setStatus(request.getStatus());
        response.setCategory(request.getCategory());
        response.setPriority(request.getPriority());
        response.setDescription(request.getDescription());
        response.setCreatedAt(request.getCreatedAt());

        return response;
    }
}