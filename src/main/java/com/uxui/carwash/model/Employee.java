package com.uxui.carwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uxui.carwash.model.security.User;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(name = "salary")
    @NotNull(message = "Salary is mandatory.")
    @Min(value = 1500, message = "Salary must be at least 1500.")
    @Max(value = 50000, message = "Salary must be at most 50000.")
    private Double salary;

    @Column(name = "hire_date")
    @NotNull(message = "Hire date is mandatory.")
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE, fallbackPatterns = { "yyyy-MM-dd" })
    private LocalDate hireDate;

    @Valid
    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "employees", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}
