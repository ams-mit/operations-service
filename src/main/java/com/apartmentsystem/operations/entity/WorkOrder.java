package com.apartmentsystem.operations.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "work_order")
@Getter
@Setter
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long maintenanceRequestId;

    private Long assignedTechnicianUserId;

    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    private WorkOrder status;

    private String resolutionNotes;
}