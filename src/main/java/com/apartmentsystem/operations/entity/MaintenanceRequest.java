package com.apartmentsystem.operations.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "maintenance_request")
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long unitId;

    private Long requestedByUserId;

    private String category;

    private String priority;

    private String description;

    private String attachmentUrl;

}