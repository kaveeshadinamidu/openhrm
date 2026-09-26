package com.openhrm.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterOrganizationRequest(
        @NotBlank String organizationName,
        @NotBlank String adminFirstName,
        @NotBlank String adminLastName,
        @NotBlank @Email String adminEmail,
        @NotBlank @Size(min = 8) String adminPassword
) {
}
