package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.BookingService;
import com.apartmentsystem.operations.dto.BookingDecisionDTO;
import com.apartmentsystem.operations.dto.BookingResponseDTO;
import com.apartmentsystem.operations.dto.CreateBookingDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TENANT_RESIDENT', 'OWNER')")
    public BookingResponseDTO createBooking(
            @RequestBody CreateBookingDTO dto) {

        return bookingService.createBooking(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TENANT_RESIDENT', 'OWNER', 'APARTMENT_MANAGER', 'MAINTENANCE_COORDINATOR')")
    public List<BookingResponseDTO> getAllBookings(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null && size == null) return bookingService.getAllBookings();
        return bookingService.getAllBookings(
                PageRequest.of(page == null ? 0 : page, size == null ? 10 : size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TENANT_RESIDENT', 'OWNER', 'APARTMENT_MANAGER', 'MAINTENANCE_COORDINATOR')")
    public BookingResponseDTO getBookingById(
            @PathVariable Long id) {

        return bookingService.getBookingById(id);
    }

    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasRole('APARTMENT_MANAGER')")
    public BookingResponseDTO decideBooking(
            @PathVariable Long id,
            @RequestBody BookingDecisionDTO dto) {

        return bookingService.decideBooking(
                id,
                dto.getDecision(),
                dto.getNote(),
                dto.getDecidedByUserId()
        );
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('TENANT_RESIDENT', 'OWNER', 'APARTMENT_MANAGER')")
    public BookingResponseDTO cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String userId) {

        return bookingService.cancelBooking(id, userId);
    }
}
