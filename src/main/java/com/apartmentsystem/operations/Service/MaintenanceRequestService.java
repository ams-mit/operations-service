package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.CreateMaintenanceRequestDTO;
import com.apartmentsystem.operations.entity.MaintenanceRequest;
import com.apartmentsystem.operations.repository.MaintenanceRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequestService(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public MaintenanceRequest createRequest(CreateMaintenanceRequestDTO dto) {

        MaintenanceRequest request = new MaintenanceRequest();

        request.setUnitId(dto.getUnitId());
        request.setCategory(dto.getCategory());
        request.setPriority(dto.getPriority());
        request.setDescription(dto.getDescription());
        request.setAttachmentUrl(dto.getAttachmentUrl());

        return maintenanceRequestRepository.save(request);
    }
}