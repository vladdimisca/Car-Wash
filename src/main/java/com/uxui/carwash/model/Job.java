package com.uxui.carwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(name = "type")
    @NotNull(message = "Type is mandatory.")
    @Enumerated(EnumType.STRING)
    private JobType type;

    @Column(name = "price")
    @NotNull(message = "Price is mandatory.")
    @Positive(message = "Price must be a positive number.")
    @Max(value = 1000, message = "Price must be at most 1000.")
    private Double price;

    @Column(name = "duration_minutes")
    @NotNull(message = "Duration minutes is mandatory.")
    @Positive(message = "Duration minutes must be a positive number.")
    @Max(value = 90, message = "Duration minutes must be at most 90.")
    private Long durationMinutes;

    @Column(name = "car_type")
    @NotNull(message = "Car type is mandatory.")
    @Enumerated(EnumType.STRING)
    private CarType carType;

    @Column(name = "number_of_employees")
    @NotNull(message = "Number of employees is mandatory.")
    @Positive(message = "Number of employees must be a positive number.")
    @Max(value = 5, message = "Number of employees must be at most 5.")
    private Integer numberOfEmployees;

    @OneToMany(mappedBy = "job", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}
