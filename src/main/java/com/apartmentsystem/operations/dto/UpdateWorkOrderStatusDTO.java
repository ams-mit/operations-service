package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.WorkOrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkOrderStatusDTO {

    private WorkOrderStatus status;

    private String resolutionNotes;
}