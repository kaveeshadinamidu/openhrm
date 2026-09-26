package com.openhrm.auth;

import com.openhrm.auth.dto.AuthResponse;
import com.openhrm.auth.dto.LoginRequest;
import com.openhrm.auth.dto.RegisterOrganizationRequest;
import com.openhrm.common.exception.ApiException;
import com.openhrm.common.security.JwtService;
import com.openhrm.common.security.UserPrincipal;
import com.openhrm.employee.Employee;
import com.openhrm.employee.EmploymentStatus;
import com.openhrm.employee.EmployeeRepository;
import com.openhrm.organization.Organization;
import com.openhrm.organization.OrganizationRepository;
import com.openhrm.user.AppUser;
import com.openhrm.user.AppUserRepository;
import com.openhrm.user.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(OrganizationRepository organizationRepository,
                        EmployeeRepository employeeRepository,
                        AppUserRepository appUserRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService) {
        this.organizationRepository = organizationRepository;
        this.employeeRepository = employeeRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Creates the tenant, its first employee, and an ADMIN login in one transaction.
    @Transactional
    public AuthResponse registerOrganization(RegisterOrganizationRequest request) {
        if (appUserRepository.findByEmail(request.adminEmail()).isPresent()) {
            throw ApiException.conflict("An account with this email already exists");
        }

        Organization organization = organizationRepository.save(new Organization(request.organizationName()));

        Employee admin = new Employee(organization.getId(), request.adminFirstName(),
                request.adminLastName(), request.adminEmail());
        admin.setEmploymentStatus(EmploymentStatus.ACTIVE);
        admin = employeeRepository.save(admin);

        AppUser user = new AppUser(organization.getId(), request.adminEmail(),
                passwordEncoder.encode(request.adminPassword()), Role.ADMIN);
        user.setEmployeeId(admin.getId());
        appUserRepository.save(user);

        return AuthResponse.bearer(jwtService.generateAccessToken(new UserPrincipal(user)));
    }

    public AuthResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return AuthResponse.bearer(jwtService.generateAccessToken(principal));
    }
}
