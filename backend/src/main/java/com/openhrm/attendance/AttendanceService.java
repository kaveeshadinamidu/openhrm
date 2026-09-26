package com.openhrm.attendance;

import com.openhrm.common.exception.ApiException;
import com.openhrm.common.security.UserPrincipal;
import com.openhrm.common.tenant.TenantContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<AttendanceEntry> listForCurrentOrg() {
        UserPrincipal principal = currentPrincipal();
        boolean isPrivileged = principal.getRole().equals("ADMIN")
                || principal.getRole().equals("HR_MANAGER")
                || principal.getRole().equals("MANAGER");
        UUID organizationId = TenantContext.get();
        return isPrivileged
                ? attendanceRepository.findAllByOrganizationId(organizationId)
                : attendanceRepository.findAllByOrganizationIdAndEmployeeId(organizationId, principal.getEmployeeId());
    }

    @Transactional
    public AttendanceEntry clockIn() {
        UUID organizationId = TenantContext.get();
        UUID employeeId = currentPrincipal().getEmployeeId();
        LocalDate today = LocalDate.now();

        attendanceRepository.findByOrganizationIdAndEmployeeIdAndWorkDate(organizationId, employeeId, today)
                .ifPresent(entry -> {
                    throw ApiException.conflict("Already clocked in today");
                });

        return attendanceRepository.save(new AttendanceEntry(organizationId, employeeId, today, Instant.now()));
    }

    @Transactional
    public AttendanceEntry clockOut() {
        UUID organizationId = TenantContext.get();
        UUID employeeId = currentPrincipal().getEmployeeId();
        LocalDate today = LocalDate.now();

        AttendanceEntry entry = attendanceRepository
                .findByOrganizationIdAndEmployeeIdAndWorkDate(organizationId, employeeId, today)
                .orElseThrow(() -> ApiException.badRequest("You haven't clocked in today"));

        if (entry.getClockOut() != null) {
            throw ApiException.conflict("Already clocked out today");
        }

        entry.setClockOut(Instant.now());
        return attendanceRepository.save(entry);
    }

    @Transactional
    public AttendanceEntry approve(UUID id) {
        AttendanceEntry entry = attendanceRepository.findByIdAndOrganizationId(id, TenantContext.get())
                .orElseThrow(() -> ApiException.notFound("Attendance entry not found"));
        entry.setStatus(AttendanceStatus.APPROVED);
        return attendanceRepository.save(entry);
    }

    private UserPrincipal currentPrincipal() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
