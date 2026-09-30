package com.apartmentsystem.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateBookingDTO {

    private Long facilityId;

    private String requestedByUserId;

    private Long unitId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer guestCount;

    private String purpose;
}