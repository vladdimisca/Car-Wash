package com.uxui.carwash.service;

import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.Employee;
import com.uxui.carwash.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private static final Long ID = 1L;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

//    @Test
//    @DisplayName("Create employee - success")
//    void create_success() {
//        Employee employee = getEmployee();
//        Employee savedEmployee = getSavedEmployee();
//
//        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
//
//        Employee resultedEmployee = employeeService.create(employee);
//
//        assertNotNull(resultedEmployee);
//        assertEquals(savedEmployee.getId(), resultedEmployee.getId());
//        verify(employeeRepository, times(1)).save(employee);
//    }

//    @Test
//    @DisplayName("Create employee - existing email or phone number - failure")
//    void create_existingEmailOrPhoneNumber_failure() {
//        Employee employee = getEmployee();
//
//        assertThrows(ConflictException.class, () -> employeeService.create(employee));
//    }

    @Test
    @DisplayName("Get employee by id - success")
    void getById_success() {
        Employee employee = getSavedEmployee();

        when(employeeRepository.findById(ID)).thenReturn(Optional.of(employee));

        Employee resultedEmployee = employeeService.getById(ID);

        assertNotNull(resultedEmployee);
        assertEquals(employee, resultedEmployee);
        verify(employeeRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Get employee by id - not found - failure")
    void getById_notFound_failure() {
        when(employeeRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getById(ID));
        verify(employeeRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Get all employees - success")
    void getAll_success() {
        when(employeeRepository.findAll(Sort.by("user.userDetails.lastName").ascending())).thenReturn(List.of(getSavedEmployee()));

        List<Employee> employees = employeeService.getAll();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(employeeRepository, times(1)).findAll(Sort.by("user.userDetails.lastName").ascending());
    }

//    @Test
//    @DisplayName("Update employee by id - success")
//    void update_success() {
//        Employee employee = getSavedEmployee();
//        Employee updatedEmployee = getSavedEmployee();
//
//        when(employeeRepository.findById(ID)).thenReturn(Optional.of(employee));
//        when(employeeRepository.save(employee)).thenReturn(updatedEmployee);
//
//        Employee resultedEmployee = employeeService.update(ID, updatedEmployee);
//
//        assertNotNull(resultedEmployee);
//        assertEquals(updatedEmployee.getId(), resultedEmployee.getId());
//    }

    @Test
    @DisplayName("Delete employee by id - success")
    void delete_success() {
        Employee employee = getSavedEmployee();

        when(employeeRepository.findById(ID)).thenReturn(Optional.of(employee));

        employeeService.deleteById(ID);

        verify(employeeRepository, times(1)).findById(ID);
        verify(employeeRepository, times(1)).delete(employee);
    }

    private Employee getEmployee() {
        Employee employee = new Employee();

        return employee;
    }

    private Employee getSavedEmployee() {
        Employee savedEmployee = getEmployee();
        savedEmployee.setId(ID);

        return savedEmployee;
    }
}
