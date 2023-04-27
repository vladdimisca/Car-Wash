package com.uxui.carwash.service;

import com.uxui.carwash.error.ErrorMessage;
import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.Employee;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    @Transactional
    public Employee create(Employee employee) {
        userService.create(employee.getUser(), "ROLE_EMPLOYEE");
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Long id, Employee employee) {
        Employee existingEmployee = getById(id);
        copyValues(existingEmployee, employee);
        User updatedUser = userService.update(existingEmployee.getUser().getId(), employee.getUser());
        existingEmployee.setUser(updatedUser);

        return employeeRepository.save(existingEmployee);
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND, "employee", id));
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll(Sort.by("user.userDetails.lastName").ascending());
    }

    public void deleteById(Long id) {
        Employee employee = getById(id);
        employeeRepository.delete(employee);
    }


    private void copyValues(Employee to, Employee from) {
        to.setHireDate(from.getHireDate());
        to.setSalary(from.getSalary());
    }
}
