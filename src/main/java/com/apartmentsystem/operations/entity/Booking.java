package com.apartmentsystem.operations.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long facilityId;

    private Long requestedByUserId;

    private Long unitId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer guestCount;

    private String purpose;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Long decidedByUserId;

    private String decisionNote;

    private LocalDateTime decidedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
