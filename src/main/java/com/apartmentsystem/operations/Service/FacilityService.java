package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.CreateFacilityDTO;
import com.apartmentsystem.operations.dto.FacilityAvailabilityDTO;
import com.apartmentsystem.operations.dto.FacilityResponseDTO;
import com.apartmentsystem.operations.entity.Facility;
import com.apartmentsystem.operations.entity.FacilityStatus;
import com.apartmentsystem.operations.repository.FacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public FacilityResponseDTO createFacility(CreateFacilityDTO dto) {

        Facility facility = new Facility();

        facility.setName(dto.getName());
        facility.setType(dto.getType());
        facility.setCapacity(dto.getCapacity());
        facility.setLocationNote(dto.getLocationNote());
        facility.setOpensAt(dto.getOpensAt());
        facility.setClosesAt(dto.getClosesAt());
        facility.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        facility.setMaxAdvanceDays(dto.getMaxAdvanceDays());
        facility.setRequiresApproval(dto.getRequiresApproval());

        facility.setStatus(FacilityStatus.ACTIVE);

        Facility savedFacility = facilityRepository.save(facility);

        return convertToResponseDTO(savedFacility);
    }

    public List<FacilityResponseDTO> getAllFacilities() {

        return facilityRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public List<FacilityResponseDTO> getAllFacilities(Pageable pageable) {
        return facilityRepository.findAll(pageable)
                .map(this::convertToResponseDTO).toList();
    }

    public FacilityResponseDTO getFacilityById(Long id) {

        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Facility not found"));

        return convertToResponseDTO(facility);
    }

    public FacilityResponseDTO updateFacility(
            Long id,
            CreateFacilityDTO dto) {

        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Facility not found"));

        facility.setName(dto.getName());
        facility.setType(dto.getType());
        facility.setCapacity(dto.getCapacity());
        facility.setLocationNote(dto.getLocationNote());
        facility.setOpensAt(dto.getOpensAt());
        facility.setClosesAt(dto.getClosesAt());
        facility.setSlotDurationMinutes(
                dto.getSlotDurationMinutes());
        facility.setMaxAdvanceDays(
                dto.getMaxAdvanceDays());
        facility.setRequiresApproval(
                dto.getRequiresApproval());

        Facility updatedFacility =
                facilityRepository.save(facility);

        return convertToResponseDTO(updatedFacility);
    }

    // Facility availability
    public FacilityAvailabilityDTO getAvailability(
            Long id,
            LocalDate date) {

        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Facility not found"));

        FacilityAvailabilityDTO response =
                new FacilityAvailabilityDTO();

        response.setFacilityId(facility.getId());
        response.setFacilityName(facility.getName());
        response.setDate(date);
        response.setOpensAt(facility.getOpensAt());
        response.setClosesAt(facility.getClosesAt());
        response.setCapacity(facility.getCapacity());
        response.setSlotDurationMinutes(
                facility.getSlotDurationMinutes());

        if (facility.getStatus() != FacilityStatus.ACTIVE) {

            response.setAvailable(false);
            response.setMessage(
                    "Facility is not available because its status is "
                            + facility.getStatus());

        } else {

            response.setAvailable(true);
            response.setMessage(
                    "Facility is available during its operating hours");
        }

        return response;
    }

    private FacilityResponseDTO convertToResponseDTO(
            Facility facility) {

        FacilityResponseDTO response =
                new FacilityResponseDTO();

        response.setId(facility.getId());
        response.setName(facility.getName());
        response.setType(facility.getType());
        response.setCapacity(facility.getCapacity());
        response.setLocationNote(facility.getLocationNote());
        response.setOpensAt(facility.getOpensAt());
        response.setClosesAt(facility.getClosesAt());
        response.setSlotDurationMinutes(
                facility.getSlotDurationMinutes());
        response.setMaxAdvanceDays(
                facility.getMaxAdvanceDays());
        response.setRequiresApproval(
                facility.getRequiresApproval());
        response.setStatus(facility.getStatus());

        return response;
    }
}
