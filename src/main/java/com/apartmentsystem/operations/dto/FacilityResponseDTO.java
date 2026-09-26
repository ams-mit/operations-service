package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.FacilityStatus;
import com.apartmentsystem.operations.entity.FacilityType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class FacilityResponseDTO {

    private Long id;

    private String name;

    private FacilityType type;

    private Integer capacity;

    private String locationNote;

    private LocalTime opensAt;

    private LocalTime closesAt;

    private Integer slotDurationMinutes;

    private Integer maxAdvanceDays;

    private Boolean requiresApproval;

    private FacilityStatus status;
}