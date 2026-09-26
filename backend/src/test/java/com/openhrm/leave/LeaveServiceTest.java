package com.openhrm.leave;

import com.openhrm.common.exception.ApiException;
import com.openhrm.common.security.UserPrincipal;
import com.openhrm.common.tenant.TenantContext;
import com.openhrm.leave.dto.LeaveRequestDto;
import com.openhrm.user.AppUser;
import com.openhrm.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LeaveServiceTest {

    private final LeaveRequestRepository leaveRequestRepository = Mockito.mock(LeaveRequestRepository.class);
    private final LeaveService leaveService = new LeaveService(leaveRequestRepository);

    private final UUID organizationId = UUID.randomUUID();
    private final UUID employeeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.set(organizationId);
        AppUser user = new AppUser(organizationId, "emp@acme.test",
                new BCryptPasswordEncoder().encode("password"), Role.EMPLOYEE);
        user.setEmployeeId(employeeId);
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsEndDateBeforeStartDate() {
        var dto = new LeaveRequestDto(employeeId, LeaveType.ANNUAL,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(1), "vacation");

        assertThatThrownBy(() -> leaveService.create(dto)).isInstanceOf(ApiException.class);
    }

    @Test
    void employeeCannotRequestLeaveForSomeoneElse() {
        var dto = new LeaveRequestDto(UUID.randomUUID(), LeaveType.ANNUAL,
                LocalDate.now(), LocalDate.now().plusDays(1), "vacation");

        assertThatThrownBy(() -> leaveService.create(dto)).isInstanceOf(ApiException.class);
    }

    @Test
    void createsLeaveRequestForSelf() {
        when(leaveRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = new LeaveRequestDto(employeeId, LeaveType.SICK,
                LocalDate.now(), LocalDate.now().plusDays(2), "flu");
        LeaveRequest created = leaveService.create(dto);

        assertThat(created.getEmployeeId()).isEqualTo(employeeId);
        assertThat(created.getStatus()).isEqualTo(LeaveStatus.PENDING);
    }
}
