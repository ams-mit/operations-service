package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.BookingService;
import com.apartmentsystem.operations.dto.BookingDecisionDTO;
import com.apartmentsystem.operations.dto.BookingResponseDTO;
import com.apartmentsystem.operations.dto.CreateBookingDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDTO createBooking(
            @RequestBody CreateBookingDTO dto) {

        return bookingService.createBooking(dto);
    }

    @GetMapping
    public List<BookingResponseDTO> getAllBookings() {

        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getBookingById(
            @PathVariable Long id) {

        return bookingService.getBookingById(id);
    }

    @PatchMapping("/{id}/decision")
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
    public BookingResponseDTO cancelBooking(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return bookingService.cancelBooking(id, userId);
    }
}