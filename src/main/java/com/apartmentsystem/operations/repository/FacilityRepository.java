package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}