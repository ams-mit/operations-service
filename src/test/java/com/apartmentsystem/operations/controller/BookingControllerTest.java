package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.BookingService;
import com.apartmentsystem.operations.dto.BookingResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import com.apartmentsystem.operations.config.SecurityConfig;
import com.apartmentsystem.operations.security.JwtAuthFilter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityConfig.class, JwtAuthFilter.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;


    @Test
    @WithMockUser(username = "10", roles = "RESIDENT")
    void createBooking_shouldReturnOk() throws Exception {

        BookingResponseDTO response = new BookingResponseDTO();

        when(bookingService.createBooking(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "facilityId": 1,
                                  "unitId": 101,
                                  "startTime": "2026-10-01T10:00:00",
                                  "endTime": "2026-10-01T11:00:00",
                                  "guestCount": 5,
                                  "purpose": "Family event"
                                }
                                """)
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "10", roles = "RESIDENT")
    void getAllBookings_shouldReturnOk() throws Exception {

        when(bookingService.getAllBookings())
                .thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/v1/bookings")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "10", roles = "RESIDENT")
    void getAllBookings_withPagination_shouldReturnOk() throws Exception {

        when(bookingService.getAllBookings(any()))
                .thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/api/v1/bookings")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "10", roles = "RESIDENT")
    void getBookingById_shouldReturnOk() throws Exception {

        BookingResponseDTO response = new BookingResponseDTO();

        when(bookingService.getBookingById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/bookings/1")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "10", roles = "MANAGER")
    void decideBooking_shouldReturnOk() throws Exception {

        BookingResponseDTO response = new BookingResponseDTO();

        when(bookingService.decideBooking(
                eq(1L),
                eq("APPROVED"),
                eq("Approved by manager"),
                eq(10L)
        )).thenReturn(response);

        mockMvc.perform(
                        patch("/api/v1/bookings/1/decision")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "decision": "APPROVED",
                                  "note": "Approved by manager",
                                  "decidedByUserId": 10
                                }
                                """)
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "10", roles = "RESIDENT")
    void cancelBooking_shouldReturnOk() throws Exception {

        BookingResponseDTO response = new BookingResponseDTO();

        when(bookingService.cancelBooking(1L, 10L))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/bookings/1/cancel")
                                .param("userId", "10")
                )
                .andExpect(status().isOk());
    }
}
