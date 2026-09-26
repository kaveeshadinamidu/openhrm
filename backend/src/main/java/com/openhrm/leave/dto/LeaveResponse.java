package com.openhrm.leave.dto;

import com.openhrm.leave.LeaveRequest;
import com.openhrm.leave.LeaveStatus;
import com.openhrm.leave.LeaveType;

import java.time.LocalDate;
import java.util.UUID;

public record LeaveResponse(
        UUID id,
        UUID employeeId,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        LeaveStatus status,
        String reason,
        UUID approverId
) {
    public static LeaveResponse from(LeaveRequest request) {
        return new LeaveResponse(
                request.getId(),
                request.getEmployeeId(),
                request.getLeaveType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus(),
                request.getReason(),
                request.getApproverId()
        );
    }
}
