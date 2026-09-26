package com.apartmentsystem.operations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTechnicianDTO {

    @Schema(
            description = "ID of the technician to assign to the work order",
            example = "25"
    )
    private Long technicianUserId;
}
