package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.BookingResponseDTO;
import com.apartmentsystem.operations.dto.CreateBookingDTO;
import com.apartmentsystem.operations.entity.Booking;
import com.apartmentsystem.operations.entity.BookingStatus;
import com.apartmentsystem.operations.entity.Facility;
import com.apartmentsystem.operations.entity.FacilityStatus;
import com.apartmentsystem.operations.entity.FacilityType;
import com.apartmentsystem.operations.repository.BookingRepository;
import com.apartmentsystem.operations.repository.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FacilityRepository facilityRepository;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService =
                new BookingService(
                        bookingRepository,
                        facilityRepository);
    }

    private Facility createActiveFacility() {

        Facility facility = new Facility();

        facility.setId(1L);
        facility.setName("Gym");
        facility.setType(FacilityType.GYM);
        facility.setStatus(FacilityStatus.ACTIVE);
        facility.setCapacity(20);

        return facility;
    }

    private CreateBookingDTO createValidBookingDTO() {

        CreateBookingDTO dto = new CreateBookingDTO();

        dto.setFacilityId(1L);
        dto.setUnitId(10L);
        dto.setStartTime(
                LocalDateTime.of(2026, 10, 1, 10, 0));
        dto.setEndTime(
                LocalDateTime.of(2026, 10, 1, 11, 0));
        dto.setGuestCount(5);
        dto.setPurpose("Meeting");

        return dto;
    }

    @Test
    void createBooking_shouldCreatePendingBooking() {

        Facility facility = createActiveFacility();
        CreateBookingDTO dto = createValidBookingDTO();

        when(facilityRepository.findById(1L))
                .thenReturn(Optional.of(facility));

        when(bookingRepository
                .findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        eq(BookingStatus.APPROVED)))
                .thenReturn(List.of());

        Booking savedBooking = new Booking();
        savedBooking.setId(100L);
        savedBooking.setFacilityId(1L);
        savedBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        try (MockedStatic<com.apartmentsystem.operations.security.CurrentUser>
                     currentUser =
                     mockStatic(
                             com.apartmentsystem.operations.security.CurrentUser.class)) {

            currentUser.when(
                            com.apartmentsystem.operations.security.CurrentUser::id)
                    .thenReturn(50L);

            BookingResponseDTO result =
                    bookingService.createBooking(dto);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals(
                    BookingStatus.PENDING,
                    result.getStatus());

            verify(bookingRepository)
                    .save(any(Booking.class));
        }
    }

    @Test
    void createBooking_shouldRejectWhenFacilityInactive() {

        Facility facility = createActiveFacility();
        facility.setStatus(FacilityStatus.INACTIVE);

        CreateBookingDTO dto = createValidBookingDTO();

        when(facilityRepository.findById(1L))
                .thenReturn(Optional.of(facility));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> bookingService.createBooking(dto));

        assertEquals(
                "Facility is not available for booking",
                exception.getMessage());

        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void createBooking_shouldRejectInvalidTime() {

        Facility facility = createActiveFacility();
        CreateBookingDTO dto = createValidBookingDTO();

        dto.setEndTime(
                LocalDateTime.of(2026, 10, 1, 9, 0));

        when(facilityRepository.findById(1L))
                .thenReturn(Optional.of(facility));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> bookingService.createBooking(dto));

        assertEquals(
                "End time must be after start time",
                exception.getMessage());
    }

    @Test
    void createBooking_shouldRejectOverlappingBooking() {

        Facility facility = createActiveFacility();
        CreateBookingDTO dto = createValidBookingDTO();

        when(facilityRepository.findById(1L))
                .thenReturn(Optional.of(facility));

        Booking existingBooking = new Booking();

        existingBooking.setId(200L);
        existingBooking.setStatus(
                BookingStatus.APPROVED);

        when(bookingRepository
                .findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(existingBooking));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> bookingService.createBooking(dto));

        assertEquals(
                "Facility is already booked for the requested time slot",
                exception.getMessage());

        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void createBooking_shouldAllowAdjacentBooking() {

        Facility facility = createActiveFacility();
        CreateBookingDTO dto = createValidBookingDTO();

        when(facilityRepository.findById(1L))
                .thenReturn(Optional.of(facility));

        /*
         * Existing booking:
         * 09:00 -> 10:00
         *
         * New booking:
         * 10:00 -> 11:00
         *
         * These are adjacent and must NOT overlap.
         */

        when(bookingRepository
                .findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        eq(BookingStatus.APPROVED)))
                .thenReturn(List.of());

        Booking savedBooking = new Booking();

        savedBooking.setId(101L);
        savedBooking.setFacilityId(1L);
        savedBooking.setStatus(
                BookingStatus.PENDING);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        try (MockedStatic<com.apartmentsystem.operations.security.CurrentUser>
                     currentUser =
                     mockStatic(
                             com.apartmentsystem.operations.security.CurrentUser.class)) {

            currentUser.when(
                            com.apartmentsystem.operations.security.CurrentUser::id)
                    .thenReturn(50L);

            BookingResponseDTO result =
                    bookingService.createBooking(dto);

            assertNotNull(result);

            assertEquals(
                    BookingStatus.PENDING,
                    result.getStatus());

            verify(bookingRepository)
                    .save(any(Booking.class));
        }
    }
}