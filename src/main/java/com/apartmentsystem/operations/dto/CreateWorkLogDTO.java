package com.apartmentsystem.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkLogDTO {

    private Long workOrderId;

    private String technicianId;

    private String note;
}