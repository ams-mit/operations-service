package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.Booking;
import com.apartmentsystem.operations.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByRequestedByUserId(Long requestedByUserId, Pageable pageable);

    List<Booking> findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
            Long facilityId,
            LocalDateTime endTime,
            LocalDateTime startTime,
            BookingStatus status
    );

    @Query("select b.facilityId as facilityId, count(b) as total from Booking b " +
            "where b.startTime < :toExclusive and b.endTime > :fromInclusive " +
            "and b.status in (com.apartmentsystem.operations.entity.BookingStatus.APPROVED, " +
            "com.apartmentsystem.operations.entity.BookingStatus.COMPLETED) " +
            "group by b.facilityId")
    List<FacilityUtilizationProjection> countUtilizedBookings(
            @Param("fromInclusive") LocalDateTime fromInclusive,
            @Param("toExclusive") LocalDateTime toExclusive);
}
