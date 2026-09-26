package com.apartmentsystem.operations;

import com.apartmentsystem.operations.controller.ReportController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import com.apartmentsystem.operations.security.JwtAuthFilter;
import com.apartmentsystem.operations.repository.MaintenanceRequestRepository;
import com.apartmentsystem.operations.repository.WorkOrderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SecurityAuthorizationTest {

    @Autowired ReportController reportController;
    @Autowired MaintenanceRequestRepository maintenanceRequestRepository;
    @Autowired WorkOrderRepository workOrderRepository;

    @Test
    void missingJwtReturnsUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/facilities");
        request.setServletPath("/api/v1/facilities");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new JwtAuthFilter().doFilter(request, response, new MockFilterChain());
        assertEquals(401, response.getStatus());
    }

    @Test
    @WithMockUser(username = "101", roles = "TECHNICIAN")
    void technicianIsForbiddenFromCoordinatorManagerReportEndpoint() {
        assertThrows(AccessDeniedException.class,
                () -> reportController.getMaintenanceSummary("status"));
    }

    @Test
    @WithMockUser(username = "101", roles = "MANAGER")
    void managerRoleIsAllowedThroughReportAuthorization() {
        assertDoesNotThrow(() -> reportController.getMaintenanceSummary("status"));
    }

    @Test
    void residentAndTechnicianRepositoryScopesExecute() {
        assertDoesNotThrow(() -> maintenanceRequestRepository.findFiltered(
                null, null, null, 101L, Pageable.unpaged()));
        assertDoesNotThrow(() -> workOrderRepository.findFiltered(
                101L, null, Pageable.unpaged()));
    }
}
