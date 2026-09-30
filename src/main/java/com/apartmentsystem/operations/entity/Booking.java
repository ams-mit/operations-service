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

    @Column(name = "requested_by_user_id", length = 36)
    private String requestedByUserId;

    private Long unitId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer guestCount;

    private String purpose;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "decided_by_user_id", length = 36)
    private String decidedByUserId;

    private String decisionNote;

    private LocalDateTime decidedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
