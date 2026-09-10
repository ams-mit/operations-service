package com.apartmentsystem.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateWorkOrderDTO {

    private Long maintenanceRequestId;

    private Long assignedTechnicianUserId;

    private LocalDate scheduledDate;
}