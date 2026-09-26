package com.openhrm.leave.dto;

import com.openhrm.leave.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record LeaveRequestDto(
        @NotNull UUID employeeId,
        @NotNull LeaveType leaveType,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull LocalDate endDate,
        String reason
) {
}
