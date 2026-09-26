package com.apartmentsystem.operations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateWorkOrderDTO {

    @Schema(
            description = "ID of the maintenance request associated with this work order",
            example = "1"
    )
    private Long maintenanceRequestId;

    @Schema(
            description = "ID of the technician assigned to the work order",
            example = "25"
    )
    private Long assignedTechnicianUserId;

    @Schema(
            description = "Date scheduled for the work order",
            example = "2026-10-01"
    )
    private LocalDate scheduledDate;
}