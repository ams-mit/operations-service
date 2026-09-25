package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMaintenanceRequestStatusDTO {
    private MaintenanceRequestStatus status;
}
