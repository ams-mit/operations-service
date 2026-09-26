package com.apartmentsystem.operations;

import com.apartmentsystem.operations.entity.Booking;
import com.apartmentsystem.operations.entity.BookingStatus;
import com.apartmentsystem.operations.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
class OperationsServiceApplicationTests {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void applicationContextLoads() {
        assertNotNull(bookingRepository);
    }

    @Test
    void shouldSaveAndRetrieveBookingThroughFullSpringContext() {

        Booking booking = new Booking();

        booking.setFacilityId(999L);
        booking.setRequestedByUserId(999L);
        booking.setUnitId(999L);

        booking.setStartTime(
                LocalDateTime.of(2026, 12, 1, 10, 0)
        );

        booking.setEndTime(
                LocalDateTime.of(2026, 12, 1, 11, 0)
        );

        booking.setGuestCount(2);
        booking.setPurpose("Integration test booking");
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.saveAndFlush(booking);

        assertNotNull(savedBooking.getId());

        assertTrue(
                bookingRepository.findById(savedBooking.getId()).isPresent()
        );
    }
}