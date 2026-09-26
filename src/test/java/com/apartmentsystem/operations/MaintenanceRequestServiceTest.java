package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.dto.UpdateMaintenanceRequestStatusDTO;
import com.apartmentsystem.operations.entity.MaintenanceRequest;
import com.apartmentsystem.operations.entity.MaintenanceRequestStatus;
import com.apartmentsystem.operations.exception.InvalidStatusTransitionException;
import com.apartmentsystem.operations.repository.MaintenanceRequestRepository;
import com.apartmentsystem.operations.repository.StatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceRequestServiceTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Mock
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @InjectMocks
    private MaintenanceRequestService maintenanceRequestService;

    @Test
    void shouldAllowSubmittedToAcknowledged() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_COORDINATOR")))
        );

        MaintenanceRequest request = new MaintenanceRequest();
        request.setId(1L);
        request.setStatus(MaintenanceRequestStatus.SUBMITTED);

        UpdateMaintenanceRequestStatusDTO dto =
                new UpdateMaintenanceRequestStatusDTO();
        dto.setStatus(MaintenanceRequestStatus.ACKNOWLEDGED);

        when(maintenanceRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(maintenanceRequestRepository.save(request))
                .thenReturn(request);

        var response =
                maintenanceRequestService.updateStatus(1L, dto);

        assertEquals(
                MaintenanceRequestStatus.ACKNOWLEDGED,
                response.getStatus()
        );

        verify(maintenanceRequestRepository).save(request);
    }

    @Test
    void shouldRejectInvalidStatusTransition() {

        MaintenanceRequest request = new MaintenanceRequest();
        request.setId(1L);
        request.setStatus(MaintenanceRequestStatus.SUBMITTED);

        UpdateMaintenanceRequestStatusDTO dto =
                new UpdateMaintenanceRequestStatusDTO();
        dto.setStatus(MaintenanceRequestStatus.CLOSED);

        when(maintenanceRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> maintenanceRequestService.updateStatus(1L, dto)
        );

        verify(maintenanceRequestRepository, never())
                .save(any(MaintenanceRequest.class));
    }
}
