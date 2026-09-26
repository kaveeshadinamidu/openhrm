package com.openhrm.auth;

import com.openhrm.auth.dto.AuthResponse;
import com.openhrm.auth.dto.LoginRequest;
import com.openhrm.auth.dto.RegisterOrganizationRequest;
import com.openhrm.employee.dto.EmployeeRequest;
import com.openhrm.employee.dto.EmployeeResponse;
import com.openhrm.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AuthAndEmployeeFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    void registersOrganizationThenCreatesAndListsEmployees() {
        var registerRequest = new RegisterOrganizationRequest(
                "Acme Startup", "Ada", "Admin", "ada@acme.test", "supersecret1");

        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                baseUrl("/api/v1/auth/register-organization"), registerRequest, AuthResponse.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String token = registerResponse.getBody().accessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var newEmployee = new EmployeeRequest("Grace", "Hopper", "grace@acme.test",
                "Engineer", "Engineering", null, null);
        ResponseEntity<EmployeeResponse> createResponse = restTemplate.exchange(
                baseUrl("/api/v1/employees"), org.springframework.http.HttpMethod.POST,
                new HttpEntity<>(newEmployee, headers), EmployeeResponse.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<EmployeeResponse[]> listResponse = restTemplate.exchange(
                baseUrl("/api/v1/employees"), org.springframework.http.HttpMethod.GET,
                new HttpEntity<>(headers), EmployeeResponse[].class);
        assertThat(listResponse.getBody()).hasSize(2); // the admin's own employee record + Grace
    }

    @Test
    void loginWithWrongPasswordIsRejected() {
        restTemplate.postForEntity(baseUrl("/api/v1/auth/register-organization"),
                new RegisterOrganizationRequest("Beta Inc", "Bea", "Boss", "bea@beta.test", "correcthorse1"),
                AuthResponse.class);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(baseUrl("/api/v1/auth/login"),
                new LoginRequest("bea@beta.test", "wrong-password"), String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
