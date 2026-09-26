package com.apartmentsystem.operations.dto;

import com.apartmentsystem.operations.entity.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDTO {

    private Long id;

    private Long facilityId;

    private Long requestedByUserId;

    private Long unitId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer guestCount;

    private String purpose;

    private BookingStatus status;

    private Long decidedByUserId;

    private String decisionNote;

    private LocalDateTime decidedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}