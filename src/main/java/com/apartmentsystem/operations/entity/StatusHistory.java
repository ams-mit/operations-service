package com.apartmentsystem.operations.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long maintenanceRequestId;

    @Enumerated(EnumType.STRING)
    private MaintenanceRequestStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private MaintenanceRequestStatus newStatus;

    private Long changedByUserId;

    private LocalDateTime changedAt;
}