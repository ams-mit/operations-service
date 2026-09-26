package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.WorkOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkOrderStatusDTO {

    @Schema(
            description = "New status of the work order",
            example = "IN_PROGRESS"
    )
    private WorkOrderStatus status;

    @Schema(
            description = "Notes describing the resolution or work performed",
            example = "Technician identified and repaired the leaking pipe"
    )
    private String resolutionNotes;
}