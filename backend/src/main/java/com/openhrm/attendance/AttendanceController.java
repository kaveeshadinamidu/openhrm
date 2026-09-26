package com.openhrm.attendance;

import com.openhrm.attendance.dto.AttendanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<AttendanceResponse> list() {
        return attendanceService.listForCurrentOrg().stream().map(AttendanceResponse::from).toList();
    }

    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceResponse> clockIn() {
        return ResponseEntity.status(HttpStatus.CREATED).body(AttendanceResponse.from(attendanceService.clockIn()));
    }

    @PostMapping("/clock-out")
    public AttendanceResponse clockOut() {
        return AttendanceResponse.from(attendanceService.clockOut());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'MANAGER')")
    public AttendanceResponse approve(@PathVariable UUID id) {
        return AttendanceResponse.from(attendanceService.approve(id));
    }
}
