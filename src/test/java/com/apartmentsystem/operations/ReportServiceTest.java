package com.apartmentsystem.operations;

import com.apartmentsystem.operations.Service.ReportService;
import com.apartmentsystem.operations.entity.*;
import com.apartmentsystem.operations.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock MaintenanceRequestRepository maintenanceRepository;
    @Mock FacilityRepository facilityRepository;
    @Mock BookingRepository bookingRepository;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(maintenanceRepository, facilityRepository, bookingRepository);
    }

    @Test
    void summarizesByStatus() {
        MaintenanceSummaryProjection row = row("SUBMITTED", 3);
        when(maintenanceRepository.countGroupedByStatus()).thenReturn(List.of(row));
        var result = reportService.getMaintenanceSummary("status");
        assertEquals("SUBMITTED", result.getFirst().getGroup());
        assertEquals(3, result.getFirst().getCount());
    }

    @Test
    void summarizesByPriority() {
        MaintenanceSummaryProjection row = row("HIGH", 2);
        when(maintenanceRepository.countGroupedByPriority()).thenReturn(List.of(row));
        assertEquals(2, reportService.getMaintenanceSummary("priority").getFirst().getCount());
    }

    @Test
    void summarizesByCategory() {
        MaintenanceSummaryProjection row = row("PLUMBING", 4);
        when(maintenanceRepository.countGroupedByCategory()).thenReturn(List.of(row));
        assertEquals("PLUMBING", reportService.getMaintenanceSummary("category").getFirst().getGroup());
    }

    @Test
    void rejectsUnknownGrouping() {
        assertThrows(ResponseStatusException.class, () -> reportService.getMaintenanceSummary("unit"));
        verifyNoInteractions(maintenanceRepository);
    }

    @Test
    void countsConfirmedFacilityBookingsWithinInclusiveDateRange() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 3);
        Facility facility = new Facility();
        facility.setId(7L);
        facility.setName("Pool");
        FacilityUtilizationProjection row = mock(FacilityUtilizationProjection.class);
        when(row.getFacilityId()).thenReturn(7L);
        when(row.getTotal()).thenReturn(2L);
        when(bookingRepository.countUtilizedBookings(from.atStartOfDay(), to.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(row));
        when(facilityRepository.findAll()).thenReturn(List.of(facility));

        var result = reportService.getFacilityUtilization(from, to);
        assertEquals(7L, result.getFirst().getFacilityId());
        assertEquals(2L, result.getFirst().getBookingCount());
        assertEquals(from, result.getFirst().getFrom());
        assertEquals(to, result.getFirst().getTo());
    }

    private MaintenanceSummaryProjection row(Object label, long total) {
        MaintenanceSummaryProjection row = mock(MaintenanceSummaryProjection.class);
        when(row.getLabel()).thenReturn(label);
        when(row.getTotal()).thenReturn(total);
        return row;
    }
}
