package com.apartmentsystem.operations.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "facility")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityType type;

    private Integer capacity;

    private String locationNote;

    private LocalTime opensAt;

    private LocalTime closesAt;

    private Integer slotDurationMinutes;

    private Integer maxAdvanceDays;

    private Boolean requiresApproval;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityStatus status;
}