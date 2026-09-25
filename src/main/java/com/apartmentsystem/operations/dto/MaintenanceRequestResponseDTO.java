package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaintenanceRequestResponseDTO {

    @Schema(
            description = "Unique ID of the maintenance request",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Current status of the maintenance request",
            example = "SUBMITTED"
    )
    private MaintenanceRequestStatus status;

    @Schema(
            description = "Category of the maintenance issue",
            example = "PLUMBING"
    )
    private String category;

    @Schema(
            description = "Priority of the maintenance request",
            example = "HIGH"
    )
    private String priority;

    @Schema(
            description = "Description of the maintenance issue",
            example = "Water leaking from the kitchen sink"
    )
    private String description;

    @Schema(
            description = "Date and time when the maintenance request was created",
            example = "2026-09-25T10:30:00"
    )
    private LocalDateTime createdAt;
}