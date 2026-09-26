package com.openhrm.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceEntry, UUID> {

    Optional<AttendanceEntry> findByOrganizationIdAndEmployeeIdAndWorkDate(
            UUID organizationId, UUID employeeId, LocalDate workDate);

    List<AttendanceEntry> findAllByOrganizationId(UUID organizationId);

    List<AttendanceEntry> findAllByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);

    Optional<AttendanceEntry> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
