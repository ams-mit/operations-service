package com.apartmentsystem.operations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMaintenanceRequestDTO {

    @Schema(description = "ID of the unit where the maintenance issue exists", example = "101")
    private Long unitId;

    @Schema(description = "ID of the user submitting the request", example = "42")
    private Long requestedByUserId;

    @Schema(description = "Category of the maintenance issue", example = "PLUMBING")
    private String category;

    @Schema(description = "Priority of the maintenance request", example = "HIGH")
    private String priority;

    @Schema(description = "Description of the maintenance issue", example = "Water leaking from the kitchen sink")
    private String description;

    @Schema(description = "URL of an attachment related to the maintenance issue", example = "https://example.com/image.jpg")
    private String attachmentUrl;
}
