package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.FacilityUtilizationDTO;
import com.apartmentsystem.operations.dto.MaintenanceSummaryDTO;
import com.apartmentsystem.operations.repository.BookingRepository;
import com.apartmentsystem.operations.repository.FacilityRepository;
import com.apartmentsystem.operations.repository.MaintenanceRequestRepository;
import com.apartmentsystem.operations.repository.MaintenanceSummaryProjection;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final FacilityRepository facilityRepository;
    private final BookingRepository bookingRepository;

    public ReportService(MaintenanceRequestRepository maintenanceRequestRepository,
                         FacilityRepository facilityRepository,
                         BookingRepository bookingRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.facilityRepository = facilityRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<MaintenanceSummaryDTO> getMaintenanceSummary(String groupBy) {
        List<MaintenanceSummaryProjection> rows = switch (groupBy == null ? "" : groupBy.toLowerCase()) {
            case "status" -> maintenanceRequestRepository.countGroupedByStatus();
            case "priority" -> maintenanceRequestRepository.countGroupedByPriority();
            case "category" -> maintenanceRequestRepository.countGroupedByCategory();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "groupBy must be one of: status, priority, category");
        };
        return rows.stream()
                .map(row -> new MaintenanceSummaryDTO(
                        row.getLabel() == null ? null : row.getLabel().toString(), row.getTotal()))
                .toList();
    }

    public List<FacilityUtilizationDTO> getFacilityUtilization(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "from and to are required and to must not be before from");
        }
        Map<Long, Long> counts = bookingRepository.countUtilizedBookings(
                        from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream().collect(Collectors.toMap(
                        row -> row.getFacilityId(), row -> row.getTotal()));
        return facilityRepository.findAll().stream().map(facility -> {
            FacilityUtilizationDTO dto = new FacilityUtilizationDTO();
            dto.setFacilityId(facility.getId());
            dto.setFacilityName(facility.getName());
            dto.setFrom(from);
            dto.setTo(to);
            dto.setBookingCount(counts.getOrDefault(facility.getId(), 0L));
            return dto;
        }).toList();
    }
}
