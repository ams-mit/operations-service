package com.apartmentsystem.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FacilityUtilizationDTO {
    private Long facilityId;
    private String facilityName;
    private LocalDate from;
    private LocalDate to;
    private long bookingCount;
}
