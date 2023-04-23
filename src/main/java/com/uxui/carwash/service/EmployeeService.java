package com.uxui.carwash.service;

import com.uxui.carwash.error.ErrorMessage;
import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.Employee;
import com.uxui.carwash.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee create(Employee employee) {
        checkEmployeeNotExisting(employee);
        return employeeRepository.save(employee);
    }

    public Employee update(Long id, Employee employee) {
        Employee existingEmployee = getById(id);
        if (!existingEmployee.getEmail().equals(employee.getEmail())) {
            checkEmailNotExisting(employee);
        }
        if (!existingEmployee.getPhoneNumber().equals(employee.getPhoneNumber())) {
            checkPhoneNumberNotExisting(employee);
        }

        copyValues(existingEmployee, employee);

        return employeeRepository.save(existingEmployee);
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND, "employee", id));
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll(Sort.by("lastName").ascending());
    }

    public void deleteById(Long id) {
        Employee employee = getById(id);
        employeeRepository.delete(employee);
    }

    private void checkEmployeeNotExisting(Employee employee) {
        if (employeeRepository.existsByEmailOrPhoneNumber(employee.getEmail(), employee.getPhoneNumber())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "employee", "email or phone number");
        }
    }

    private void checkEmailNotExisting(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "employee", "email");
        }
    }

    private void checkPhoneNumberNotExisting(Employee employee) {
        if (employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "employee", "phone number");
        }
    }

    private void copyValues(Employee to, Employee from) {
        to.setEmail(from.getEmail());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setHireDate(from.getHireDate());
        to.setSalary(from.getSalary());
        to.setPhoneNumber(from.getPhoneNumber());
    }
}
