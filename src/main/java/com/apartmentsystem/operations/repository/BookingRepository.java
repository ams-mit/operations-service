package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.Booking;
import com.apartmentsystem.operations.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
            Long facilityId,
            LocalDateTime endTime,
            LocalDateTime startTime,
            BookingStatus status
    );
}