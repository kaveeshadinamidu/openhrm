package com.openhrm.attendance.dto;

import com.openhrm.attendance.AttendanceEntry;
import com.openhrm.attendance.AttendanceStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AttendanceResponse(
        UUID id,
        UUID employeeId,
        LocalDate workDate,
        Instant clockIn,
        Instant clockOut,
        Long minutesWorked,
        AttendanceStatus status
) {
    public static AttendanceResponse from(AttendanceEntry entry) {
        Long minutes = entry.getClockOut() == null
                ? null
                : Duration.between(entry.getClockIn(), entry.getClockOut()).toMinutes();
        return new AttendanceResponse(entry.getId(), entry.getEmployeeId(), entry.getWorkDate(),
                entry.getClockIn(), entry.getClockOut(), minutes, entry.getStatus());
    }
}
