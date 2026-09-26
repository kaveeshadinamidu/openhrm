package com.openhrm.leave;

import com.openhrm.common.exception.ApiException;
import com.openhrm.common.security.UserPrincipal;
import com.openhrm.common.tenant.TenantContext;
import com.openhrm.leave.dto.LeaveRequestDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public List<LeaveRequest> listForCurrentOrg() {
        return leaveRequestRepository.findAllByOrganizationId(TenantContext.get());
    }

    public LeaveRequest getForCurrentOrg(UUID id) {
        return leaveRequestRepository.findByIdAndOrganizationId(id, TenantContext.get())
                .orElseThrow(() -> ApiException.notFound("Leave request not found"));
    }

    @Transactional
    public LeaveRequest create(LeaveRequestDto dto) {
        if (dto.endDate().isBefore(dto.startDate())) {
            throw ApiException.badRequest("endDate cannot be before startDate");
        }
        UserPrincipal principal = currentPrincipal();
        boolean actingForSelf = dto.employeeId().equals(principal.getEmployeeId());
        boolean isPrivileged = principal.getRole().equals("ADMIN") || principal.getRole().equals("HR_MANAGER");
        if (!actingForSelf && !isPrivileged) {
            throw ApiException.forbidden("You can only request leave for yourself");
        }

        LeaveRequest leaveRequest = new LeaveRequest(TenantContext.get(), dto.employeeId(), dto.leaveType(),
                dto.startDate(), dto.endDate(), dto.reason());
        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest approve(UUID id) {
        return decide(id, LeaveStatus.APPROVED);
    }

    @Transactional
    public LeaveRequest reject(UUID id) {
        return decide(id, LeaveStatus.REJECTED);
    }

    private LeaveRequest decide(UUID id, LeaveStatus decision) {
        LeaveRequest leaveRequest = getForCurrentOrg(id);
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw ApiException.conflict("Only pending requests can be decided on");
        }
        leaveRequest.setStatus(decision);
        leaveRequest.setApproverId(currentPrincipal().getEmployeeId());
        return leaveRequestRepository.save(leaveRequest);
    }

    private UserPrincipal currentPrincipal() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
