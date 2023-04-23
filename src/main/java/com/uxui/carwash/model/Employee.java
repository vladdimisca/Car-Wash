package com.uxui.carwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(name = "first_name")
    @NotNull(message = "First name is mandatory.")
    @Size(min = 1, max = 150, message = "First name must be between 1 and 100 characters long.")
    private String firstName;

    @Column(name = "last_name")
    @NotNull(message = "Last name is mandatory.")
    @Size(min = 1, max = 150, message = "Last name must be between 1 and 100 characters long.")
    private String lastName;

    @Column(name = "email")
    @NotEmpty(message="Email cannot be empty.")
    @Email(message = "Email has an invalid format.")
    private String email;

    @Column(name = "phone_number")
    @NotNull(message = "Phone number is mandatory.")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone number is invalid.")
    private String phoneNumber;

    @Column(name = "salary")
    @NotNull(message = "Salary is mandatory.")
    @Min(value = 1500, message = "Salary must be at least 1500.")
    @Max(value = 50000, message = "Salary must be at most 50000.")
    private Double salary;

    @Column(name = "hire_date")
    @NotNull(message = "Hire date is mandatory.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    @ManyToMany(mappedBy = "employees", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}
