package com.apartmentsystem.operations;

import com.apartmentsystem.operations.Service.*;
import com.apartmentsystem.operations.entity.*;
import com.apartmentsystem.operations.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPaginationServiceTest {

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }
    @Mock MaintenanceRequestRepository maintenanceRepository;
    @Mock StatusHistoryRepository historyRepository;
    @Mock WorkOrderRepository workOrderRepository;
    @Mock BookingRepository bookingRepository;
    @Mock FacilityRepository facilityRepository;

    @Test
    void maintenanceFiltersAndAppliesRequestedPage() {
        MaintenanceRequest request = new MaintenanceRequest();
        request.setId(5L);
        when(maintenanceRepository.findFiltered(MaintenanceRequestStatus.SUBMITTED, "HIGH", "PLUMBING",
                null, PageRequest.of(1, 1))).thenReturn(new PageImpl<>(List.of(request)));
        MaintenanceRequestService service = new MaintenanceRequestService(maintenanceRepository, historyRepository);
        var results = service.getFilteredRequests(MaintenanceRequestStatus.SUBMITTED, "HIGH", "PLUMBING",
                PageRequest.of(1, 1));
        assertEquals(5L, results.getFirst().getId());
    }

    @Test
    void paginatedWorkOrdersBookingsAndFacilitiesReturnOnlyPageContent() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", "",
                        List.of(new SimpleGrantedAuthority("ROLE_MANAGER"))));
        var pageable = PageRequest.of(0, 1);
        WorkOrder order = new WorkOrder(); order.setId(11L);
        Booking booking = new Booking(); booking.setId(12L);
        Facility facility = new Facility(); facility.setId(13L); facility.setName("Pool");
        when(workOrderRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(order)));
        when(bookingRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(booking)));
        when(facilityRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(facility)));

        assertEquals(11L, new WorkOrderService(workOrderRepository, maintenanceRepository)
                .getAllWorkOrders(pageable).getFirst().getId());
        assertEquals(12L, new BookingService(bookingRepository, facilityRepository)
                .getAllBookings(pageable).getFirst().getId());
        assertEquals(13L, new FacilityService(facilityRepository)
                .getAllFacilities(pageable).getFirst().getId());
    }
}
