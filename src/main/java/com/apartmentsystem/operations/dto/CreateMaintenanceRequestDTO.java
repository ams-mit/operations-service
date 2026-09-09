package com.apartmentsystem.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMaintenanceRequestDTO {

    private Long unitId;
    private String category;
    private String priority;
    private String description;
    private String attachmentUrl;
}