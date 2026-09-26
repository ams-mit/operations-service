package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.ReportService;
import com.apartmentsystem.operations.dto.FacilityUtilizationDTO;
import com.apartmentsystem.operations.dto.MaintenanceSummaryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/maintenance-summary")
    public List<MaintenanceSummaryDTO> getMaintenanceSummary(
            @RequestParam String groupBy) {
        return reportService.getMaintenanceSummary(groupBy);
    }

    @GetMapping("/facility-utilization")
    public List<FacilityUtilizationDTO> getFacilityUtilization(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return reportService.getFacilityUtilization(from, to);
    }
}
