package com.uxui.carwash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uxui.carwash.model.security.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(name = "type")
    @NotNull(message = "Type is mandatory.")
    @Enumerated(EnumType.STRING)
    private CarType type;

    @Column(name = "license_plate")
    @NotNull(message = "License plate is mandatory.")
    @Size(min = 1, max = 100, message = "License plate must be between 1 and 100 characters long.")
    private String licensePlate;

    @Column(name = "created_at")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "car", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @ManyToOne
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;
}
