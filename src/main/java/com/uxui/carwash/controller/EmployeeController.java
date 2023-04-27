package com.uxui.carwash.controller;

import com.uxui.carwash.model.Employee;
import com.uxui.carwash.model.UserDetails;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public String create(@Valid @ModelAttribute Employee employee, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "employee-form";
        }

        employeeService.create(employee);
        return "redirect:/employees";
    }

    @GetMapping("/form")
    public String showEmployeeForm(Model model) {
        User user = new User();
        user.setPassword("12345a");
        user.setUserDetails(new UserDetails());
        Employee employee = new Employee();
        employee.setUser(user);
        model.addAttribute("employee", employee);
        return "employee-form";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("employees", employeeService.getAll());
        return "employees";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("employee", employeeService.getById(id));
        return "employee-info";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        employeeService.deleteById(id);
        return "redirect:/employees";
    }
}
