package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.FacilityService;
import com.apartmentsystem.operations.dto.CreateFacilityDTO;
import com.apartmentsystem.operations.dto.FacilityAvailabilityDTO;
import com.apartmentsystem.operations.dto.FacilityResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @PostMapping
    public FacilityResponseDTO createFacility(
            @RequestBody CreateFacilityDTO dto) {
        return facilityService.createFacility(dto);
    }

    @GetMapping
    public List<FacilityResponseDTO> getAllFacilities() {
        return facilityService.getAllFacilities();
    }

    @GetMapping("/{id}")
    public FacilityResponseDTO getFacilityById(
            @PathVariable Long id) {
        return facilityService.getFacilityById(id);
    }

    @PutMapping("/{id}")
    public FacilityResponseDTO updateFacility(
            @PathVariable Long id,
            @RequestBody CreateFacilityDTO dto) {
        return facilityService.updateFacility(id, dto);
    }

    @GetMapping("/{id}/availability")
    public FacilityAvailabilityDTO getAvailability(
            @PathVariable Long id,
            @RequestParam LocalDate date) {

        return facilityService.getAvailability(id, date);
    }
}