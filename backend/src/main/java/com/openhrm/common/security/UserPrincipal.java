package com.openhrm.common.security;

import com.openhrm.user.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

// Adapts our AppUser entity to what Spring Security expects, plus the tenant/employee ids we need on every request.
public class UserPrincipal implements UserDetails {

    private final UUID userId;
    private final UUID organizationId;
    private final UUID employeeId;
    private final String email;
    private final String passwordHash;
    private final String role;

    public UserPrincipal(AppUser user) {
        this.userId = user.getId();
        this.organizationId = user.getOrganizationId();
        this.employeeId = user.getEmployeeId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole().name();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
