package com.apartmentsystem.operations.controller;

import com.apartmentsystem.operations.Service.WorkOrderService;
import com.apartmentsystem.operations.dto.AssignTechnicianDTO;
import com.apartmentsystem.operations.dto.CreateWorkOrderDTO;
import com.apartmentsystem.operations.dto.UpdateWorkOrderStatusDTO;
import com.apartmentsystem.operations.dto.WorkOrderResponseDTO;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-orders")
public class workOrderController {

    private final WorkOrderService workOrderService;

    public workOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    // POST - Create and assign a work order
    @Operation(
            summary = "Create a work order",
            description = "Creates and assigns a new work order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Work order created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    public WorkOrderResponseDTO createWorkOrder(
            @RequestBody CreateWorkOrderDTO dto) {

        return workOrderService.createWorkOrder(dto);
    }

    // GET - Get all work orders
    @Operation(
            summary = "Get all work orders",
            description = "Retrieves all work orders."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Work orders retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public List<WorkOrderResponseDTO> getAllWorkOrders() {

        return workOrderService.getAllWorkOrders();
    }

    // PATCH - Update work order status
    @Operation(
            summary = "Update work order status",
            description = "Updates the status and resolution notes of a work order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Work order status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status or request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Work order not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PatchMapping("/{orderId}/status")
    public WorkOrderResponseDTO updateWorkOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateWorkOrderStatusDTO dto) {

        return workOrderService.updateStatus(
                orderId,
                dto.getStatus(),
                dto.getResolutionNotes()
        );
    }

    // PATCH - Assign or reassign technician
    @Operation(
            summary = "Assign or reassign technician",
            description = "Assigns a technician to a work order or replaces the currently assigned technician."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Technician assigned successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid technician data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Work order not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PatchMapping("/{orderId}/technician")
    public WorkOrderResponseDTO assignTechnician(
            @PathVariable Long orderId,
            @RequestBody AssignTechnicianDTO dto) {

        return workOrderService.assignTechnician(
                orderId,
                dto.getTechnicianUserId()
        );
    }
}