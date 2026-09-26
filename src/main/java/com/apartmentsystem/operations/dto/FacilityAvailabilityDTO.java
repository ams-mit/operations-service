package com.apartmentsystem.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class FacilityAvailabilityDTO {

    private Long facilityId;
    private String facilityName;
    private LocalDate date;

    private LocalTime opensAt;
    private LocalTime closesAt;

    private Integer capacity;
    private Integer slotDurationMinutes;

    private Boolean available;
    private String message;
}