package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.WorkOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkOrderResponseDTO {

    private Long id;

    private Long maintenanceRequestId;

    private Long assignedTechnicianUserId;

    private LocalDate scheduledDate;

    private WorkOrderStatus status;

    private String resolutionNotes;
}