package com.openhrm.employee.dto;

import com.openhrm.employee.Employee;
import com.openhrm.employee.EmploymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String jobTitle,
        String department,
        UUID managerId,
        EmploymentStatus employmentStatus,
        LocalDate hireDate
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getJobTitle(),
                employee.getDepartment(),
                employee.getManagerId(),
                employee.getEmploymentStatus(),
                employee.getHireDate()
        );
    }
}
