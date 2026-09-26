package com.openhrm.leave;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    List<LeaveRequest> findAllByOrganizationId(UUID organizationId);

    List<LeaveRequest> findAllByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);

    Optional<LeaveRequest> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
