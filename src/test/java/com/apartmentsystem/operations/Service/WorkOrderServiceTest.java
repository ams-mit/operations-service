package com.apartmentsystem.operations.Service;

import com.apartmentsystem.operations.entity.WorkOrderStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderServiceTest {

    private final WorkOrderService workOrderService = new WorkOrderService(null, null);

    @Test
    void validWorkOrderTransitionsShouldBeAccepted() throws Exception {

        Method method = WorkOrderService.class.getDeclaredMethod(
                "isValidTransition",
                WorkOrderStatus.class,
                WorkOrderStatus.class
        );

        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.CREATED,
                WorkOrderStatus.ASSIGNED
        ));

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.ASSIGNED,
                WorkOrderStatus.IN_PROGRESS
        ));

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.IN_PROGRESS,
                WorkOrderStatus.COMPLETED
        ));

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.COMPLETED,
                WorkOrderStatus.VERIFIED
        ));
    }

    @Test
    void invalidWorkOrderTransitionsShouldBeRejected() throws Exception {

        Method method = WorkOrderService.class.getDeclaredMethod(
                "isValidTransition",
                WorkOrderStatus.class,
                WorkOrderStatus.class
        );

        method.setAccessible(true);

        assertFalse((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.CREATED,
                WorkOrderStatus.COMPLETED
        ));

        assertFalse((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.ASSIGNED,
                WorkOrderStatus.VERIFIED
        ));

        assertFalse((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.COMPLETED,
                WorkOrderStatus.IN_PROGRESS
        ));
    }

    @Test
    void reassignmentTransitionShouldBeAccepted() throws Exception {

        Method method = WorkOrderService.class.getDeclaredMethod(
                "isValidTransition",
                WorkOrderStatus.class,
                WorkOrderStatus.class
        );

        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.ASSIGNED,
                WorkOrderStatus.REASSIGNED
        ));

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.REASSIGNED,
                WorkOrderStatus.ASSIGNED
        ));
    }

    @Test
    void cancellationFromNonTerminalStateShouldBeAccepted() throws Exception {

        Method method = WorkOrderService.class.getDeclaredMethod(
                "isValidTransition",
                WorkOrderStatus.class,
                WorkOrderStatus.class
        );

        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.ASSIGNED,
                WorkOrderStatus.CANCELLED
        ));

        assertTrue((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.IN_PROGRESS,
                WorkOrderStatus.CANCELLED
        ));

        assertFalse((Boolean) method.invoke(
                workOrderService,
                WorkOrderStatus.VERIFIED,
                WorkOrderStatus.CANCELLED
        ));
    }
}

