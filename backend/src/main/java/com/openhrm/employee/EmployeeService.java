package com.openhrm.employee;

import com.openhrm.common.exception.ApiException;
import com.openhrm.common.tenant.TenantContext;
import com.openhrm.employee.dto.EmployeeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> listForCurrentOrg() {
        return employeeRepository.findAllByOrganizationId(TenantContext.get());
    }

    public Employee getForCurrentOrg(UUID employeeId) {
        return employeeRepository.findByIdAndOrganizationId(employeeId, TenantContext.get())
                .orElseThrow(() -> ApiException.notFound("Employee not found"));
    }

    @Transactional
    public Employee create(EmployeeRequest request) {
        UUID organizationId = TenantContext.get();
        if (employeeRepository.existsByOrganizationIdAndEmail(organizationId, request.email())) {
            throw ApiException.conflict("An employee with this email already exists");
        }
        Employee employee = new Employee(organizationId, request.firstName(), request.lastName(), request.email());
        applyOptionalFields(employee, request);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(UUID employeeId, EmployeeRequest request) {
        Employee employee = getForCurrentOrg(employeeId);
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        applyOptionalFields(employee, request);
        return employeeRepository.save(employee);
    }

    @Transactional
    public void offboard(UUID employeeId) {
        Employee employee = getForCurrentOrg(employeeId);
        employee.setEmploymentStatus(EmploymentStatus.OFFBOARDED);
        employeeRepository.save(employee);
    }

    private void applyOptionalFields(Employee employee, EmployeeRequest request) {
        employee.setJobTitle(request.jobTitle());
        employee.setDepartment(request.department());
        employee.setManagerId(request.managerId());
        employee.setHireDate(request.hireDate());
    }
}
