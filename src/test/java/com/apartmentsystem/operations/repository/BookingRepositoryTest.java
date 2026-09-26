package com.apartmentsystem.operations.repository;

import com.apartmentsystem.operations.entity.Booking;
import com.apartmentsystem.operations.entity.BookingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void shouldFindOverlappingApprovedBooking() {

        Booking existingBooking = new Booking();

        existingBooking.setFacilityId(1L);
        existingBooking.setRequestedByUserId(100L);
        existingBooking.setUnitId(10L);
        existingBooking.setStartTime(
                LocalDateTime.of(2026, 10, 1, 10, 0)
        );
        existingBooking.setEndTime(
                LocalDateTime.of(2026, 10, 1, 11, 0)
        );
        existingBooking.setGuestCount(5);
        existingBooking.setPurpose("Existing booking");
        existingBooking.setStatus(BookingStatus.APPROVED);
        existingBooking.setCreatedAt(LocalDateTime.now());
        existingBooking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.saveAndFlush(existingBooking);

        List<Booking> overlappingBookings =
                bookingRepository
                        .findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                                1L,
                                LocalDateTime.of(2026, 10, 1, 11, 30),
                                LocalDateTime.of(2026, 10, 1, 10, 30),
                                BookingStatus.APPROVED
                        );

        assertEquals(1, overlappingBookings.size());
    }

    @Test
    void shouldNotFindAdjacentBooking() {

        Booking existingBooking = new Booking();

        existingBooking.setFacilityId(1L);
        existingBooking.setRequestedByUserId(100L);
        existingBooking.setUnitId(10L);
        existingBooking.setStartTime(
                LocalDateTime.of(2026, 10, 1, 10, 0)
        );
        existingBooking.setEndTime(
                LocalDateTime.of(2026, 10, 1, 11, 0)
        );
        existingBooking.setGuestCount(5);
        existingBooking.setPurpose("Existing booking");
        existingBooking.setStatus(BookingStatus.APPROVED);
        existingBooking.setCreatedAt(LocalDateTime.now());
        existingBooking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.saveAndFlush(existingBooking);

        List<Booking> overlappingBookings =
                bookingRepository
                        .findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                                1L,
                                LocalDateTime.of(2026, 10, 1, 12, 0),
                                LocalDateTime.of(2026, 10, 1, 11, 0),
                                BookingStatus.APPROVED
                        );

        assertTrue(overlappingBookings.isEmpty());
    }
}