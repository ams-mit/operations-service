package com.apartmentsystem.operations.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_log_entry")
@Getter
@Setter
public class WorkLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workOrderId;

    private Long technicianId;

    @Column(nullable = false)
    private String note;

    private LocalDateTime loggedAt;
}
