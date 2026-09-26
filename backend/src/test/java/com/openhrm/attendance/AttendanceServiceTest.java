package com.openhrm.attendance;

import com.openhrm.common.exception.ApiException;
import com.openhrm.common.security.UserPrincipal;
import com.openhrm.common.tenant.TenantContext;
import com.openhrm.user.AppUser;
import com.openhrm.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AttendanceServiceTest {

    private final AttendanceRepository attendanceRepository = Mockito.mock(AttendanceRepository.class);
    private final AttendanceService attendanceService = new AttendanceService(attendanceRepository);

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
    void clockInCreatesTodaysEntry() {
        when(attendanceRepository.findByOrganizationIdAndEmployeeIdAndWorkDate(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceEntry entry = attendanceService.clockIn();

        assertThat(entry.getEmployeeId()).isEqualTo(employeeId);
        assertThat(entry.getWorkDate()).isEqualTo(LocalDate.now());
        assertThat(entry.getClockOut()).isNull();
    }

    @Test
    void cannotClockInTwiceOnTheSameDay() {
        AttendanceEntry existing = new AttendanceEntry(organizationId, employeeId, LocalDate.now(), Instant.now());
        when(attendanceRepository.findByOrganizationIdAndEmployeeIdAndWorkDate(any(), any(), any()))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(attendanceService::clockIn).isInstanceOf(ApiException.class);
    }

    @Test
    void cannotClockOutWithoutClockingIn() {
        when(attendanceRepository.findByOrganizationIdAndEmployeeIdAndWorkDate(any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(attendanceService::clockOut).isInstanceOf(ApiException.class);
    }

    @Test
    void clockOutClosesTodaysEntry() {
        AttendanceEntry existing = new AttendanceEntry(organizationId, employeeId, LocalDate.now(), Instant.now());
        when(attendanceRepository.findByOrganizationIdAndEmployeeIdAndWorkDate(any(), any(), any()))
                .thenReturn(Optional.of(existing));
        when(attendanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceEntry result = attendanceService.clockOut();

        assertThat(result.getClockOut()).isNotNull();
    }
}
