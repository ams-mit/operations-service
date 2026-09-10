package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaintenanceRequestResponseDTO {

    private Long id;
    private MaintenanceRequestStatus status;
    private String category;
    private String priority;
    private String description;
    private LocalDateTime createdAt;
}