package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaintenanceRequestResponseDTO {

    @Schema(description = "Unique ID of the maintenance request", example = "1")
    private Long id;

    @Schema(description = "ID of the unit where the maintenance issue exists", example = "101")
    private Long unitId;

    @Schema(description = "ID of the user who submitted the request", example = "42")
    private Long requestedByUserId;

    @Schema(description = "URL of an attachment related to the maintenance issue", example = "https://example.com/image.jpg")
    private String attachmentUrl;

    @Schema(description = "Current status of the maintenance request", example = "SUBMITTED")
    private MaintenanceRequestStatus status;

    @Schema(description = "Category of the maintenance issue", example = "PLUMBING")
    private String category;

    @Schema(description = "Priority of the maintenance request", example = "HIGH")
    private String priority;

    @Schema(description = "Description of the maintenance issue", example = "Water leaking from the kitchen sink")
    private String description;

    @Schema(description = "Timestamp when the request was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the request was last updated")
    private LocalDateTime updatedAt;
}