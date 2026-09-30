package com.apartmentsystem.operations;

import com.apartmentsystem.operations.Service.BookingService;
import com.apartmentsystem.operations.Service.MaintenanceRequestService;
import com.apartmentsystem.operations.Service.WorkOrderService;
import com.apartmentsystem.operations.dto.CreateBookingDTO;
import com.apartmentsystem.operations.entity.*;
import com.apartmentsystem.operations.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityScopingServiceTest {
    @Mock MaintenanceRequestRepository maintenanceRepository;
    @Mock StatusHistoryRepository statusHistoryRepository;
    @Mock WorkOrderRepository workOrderRepository;
    @Mock BookingRepository bookingRepository;
    @Mock FacilityRepository facilityRepository;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void residentMaintenanceListIsFilteredToJwtRequesterId() {
        authenticate("uuid-101", "TENANT_RESIDENT");
        when(maintenanceRepository.findFiltered(null, null, null, "uuid-101", Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of()));
        new MaintenanceRequestService(maintenanceRepository, statusHistoryRepository)
                .getFilteredRequests(null, null, null, Pageable.unpaged());
        verify(maintenanceRepository).findFiltered(null, null, null, "uuid-101", Pageable.unpaged());
    }

    @Test
    void residentCannotReadAnotherRequestersMaintenanceRequest() {
        authenticate("uuid-101", "TENANT_RESIDENT");
        MaintenanceRequest otherResidentRequest = new MaintenanceRequest();
        otherResidentRequest.setId(2L);
        otherResidentRequest.setRequestedByUserId("uuid-202");
        when(maintenanceRepository.findById(2L)).thenReturn(Optional.of(otherResidentRequest));
        assertThrows(ResponseStatusException.class,
                () -> new MaintenanceRequestService(maintenanceRepository, statusHistoryRepository)
                        .getRequestById(2L));
    }

    @Test
    void technicianIdFilterCannotOverrideJwtIdentity() {
        authenticate("uuid-101", "TECHNICIAN");
        when(workOrderRepository.findFiltered("uuid-101", WorkOrderStatus.ASSIGNED, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of()));
        new WorkOrderService(workOrderRepository, maintenanceRepository)
                .getWorkOrders("uuid-202", WorkOrderStatus.ASSIGNED, Pageable.unpaged());
        verify(workOrderRepository).findFiltered("uuid-101", WorkOrderStatus.ASSIGNED, Pageable.unpaged());
    }

    @Test
    void bookingRequesterIsTakenFromJwtRatherThanRequestBody() {
        authenticate("uuid-101", "TENANT_RESIDENT");
        Facility facility = new Facility();
        facility.setId(5L);
        facility.setStatus(FacilityStatus.ACTIVE);
        when(facilityRepository.findById(5L)).thenReturn(Optional.of(facility));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateBookingDTO dto = new CreateBookingDTO();
        dto.setFacilityId(5L);
        dto.setRequestedByUserId("uuid-202");
        dto.setStartTime(LocalDateTime.of(2026, 10, 1, 10, 0));
        dto.setEndTime(LocalDateTime.of(2026, 10, 1, 11, 0));
        dto.setGuestCount(1);
        new BookingService(bookingRepository, facilityRepository).createBooking(dto);

        ArgumentCaptor<Booking> saved = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(saved.capture());
        assertEquals("uuid-101", saved.getValue().getRequestedByUserId());
    }

    private void authenticate(String userId, String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, "",
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }
}
