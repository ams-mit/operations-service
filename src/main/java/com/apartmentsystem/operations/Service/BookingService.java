package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.BookingResponseDTO;
import com.apartmentsystem.operations.dto.CreateBookingDTO;
import com.apartmentsystem.operations.entity.Booking;
import com.apartmentsystem.operations.entity.BookingStatus;
import com.apartmentsystem.operations.entity.Facility;
import com.apartmentsystem.operations.entity.FacilityStatus;
import com.apartmentsystem.operations.repository.BookingRepository;
import com.apartmentsystem.operations.repository.FacilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FacilityRepository facilityRepository;

    public BookingService(
            BookingRepository bookingRepository,
            FacilityRepository facilityRepository) {

        this.bookingRepository = bookingRepository;
        this.facilityRepository = facilityRepository;
    }

    public BookingResponseDTO createBooking(CreateBookingDTO dto) {

        Facility facility = facilityRepository.findById(dto.getFacilityId())
                .orElseThrow(() ->
                        new RuntimeException("Facility not found"));

        if (facility.getStatus() != FacilityStatus.ACTIVE) {
            throw new RuntimeException(
                    "Facility is not available for booking");
        }

        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new RuntimeException(
                    "Start time and end time are required");
        }

        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new RuntimeException(
                    "End time must be after start time");
        }

        if (dto.getGuestCount() == null || dto.getGuestCount() <= 0) {
            throw new RuntimeException(
                    "Guest count must be greater than zero");
        }

        if (facility.getCapacity() != null
                && dto.getGuestCount() > facility.getCapacity()) {

            throw new RuntimeException(
                    "Guest count exceeds facility capacity");
        }

        if (facility.getOpensAt() != null
                && dto.getStartTime().toLocalTime()
                .isBefore(facility.getOpensAt())) {

            throw new RuntimeException(
                    "Booking starts before facility opening time");
        }

        if (facility.getClosesAt() != null
                && dto.getEndTime().toLocalTime()
                .isAfter(facility.getClosesAt())) {

            throw new RuntimeException(
                    "Booking ends after facility closing time");
        }

        List<Booking> overlappingBookings =
                bookingRepository
                        .findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                                dto.getFacilityId(),
                                dto.getEndTime(),
                                dto.getStartTime(),
                                BookingStatus.APPROVED
                        );

        if (!overlappingBookings.isEmpty()) {
            throw new RuntimeException(
                    "Facility is already booked for the requested time slot");
        }

        Booking booking = new Booking();

        booking.setFacilityId(dto.getFacilityId());
        booking.setRequestedByUserId(dto.getRequestedByUserId());
        booking.setUnitId(dto.getUnitId());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setGuestCount(dto.getGuestCount());
        booking.setPurpose(dto.getPurpose());

        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        Booking savedBooking =
                bookingRepository.save(booking);

        return convertToResponseDTO(savedBooking);
    }

    public List<BookingResponseDTO> getAllBookings() {

        return bookingRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public BookingResponseDTO getBookingById(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        return convertToResponseDTO(booking);
    }

    // Approve or reject booking
    public BookingResponseDTO decideBooking(
            Long id,
            String decision,
            String note,
            Long decidedByUserId) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending bookings can be approved or rejected");
        }

        if (!decision.equalsIgnoreCase("APPROVED")
                && !decision.equalsIgnoreCase("REJECTED")) {

            throw new RuntimeException(
                    "Decision must be APPROVED or REJECTED");
        }

        if (decision.equalsIgnoreCase("APPROVED")) {

            List<Booking> overlappingBookings =
                    bookingRepository
                            .findByFacilityIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                                    booking.getFacilityId(),
                                    booking.getEndTime(),
                                    booking.getStartTime(),
                                    BookingStatus.APPROVED
                            );

            if (!overlappingBookings.isEmpty()) {
                throw new RuntimeException(
                        "Facility is already booked for the requested time slot");
            }

            booking.setStatus(BookingStatus.APPROVED);

        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        booking.setDecidedByUserId(decidedByUserId);
        booking.setDecisionNote(note);
        booking.setDecidedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        Booking updatedBooking =
                bookingRepository.save(booking);

        return convertToResponseDTO(updatedBooking);
    }

    // Cancel booking
    public BookingResponseDTO cancelBooking(
            Long id,
            Long userId) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING
                && booking.getStatus() != BookingStatus.APPROVED) {

            throw new RuntimeException(
                    "Only pending or approved bookings can be cancelled");
        }

        if (booking.getRequestedByUserId() != null
                && !booking.getRequestedByUserId().equals(userId)) {

            throw new RuntimeException(
                    "Only the booking requester can cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        Booking updatedBooking =
                bookingRepository.save(booking);

        return convertToResponseDTO(updatedBooking);
    }

    private BookingResponseDTO convertToResponseDTO(
            Booking booking) {

        BookingResponseDTO response =
                new BookingResponseDTO();

        response.setId(booking.getId());
        response.setFacilityId(booking.getFacilityId());
        response.setRequestedByUserId(
                booking.getRequestedByUserId());
        response.setUnitId(booking.getUnitId());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setGuestCount(booking.getGuestCount());
        response.setPurpose(booking.getPurpose());
        response.setStatus(booking.getStatus());
        response.setDecidedByUserId(
                booking.getDecidedByUserId());
        response.setDecisionNote(
                booking.getDecisionNote());
        response.setDecidedAt(booking.getDecidedAt());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());

        return response;
    }
}