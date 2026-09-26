package com.apartmentsystem.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDecisionDTO {

    private String decision;

    private String note;

    private Long decidedByUserId;
}