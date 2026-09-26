package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMaintenanceRequestStatusDTO {

    @Schema(
            description = "New status of the maintenance request",
            example = "ACKNOWLEDGED"
    )
    private MaintenanceRequestStatus status;
}