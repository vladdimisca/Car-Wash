package com.uxui.carwash.model;

import com.uxui.carwash.model.security.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    @NotNull(message = "First name is mandatory.")
    @Size(min = 1, max = 150, message = "First name must be between 1 and 100 characters long.")
    private String firstName;

    @Column(name = "last_name")
    @NotNull(message = "Last name is mandatory.")
    @Size(min = 1, max = 150, message = "Last name must be between 1 and 100 characters long.")
    private String lastName;

    @Column(name = "phone_number")
    @NotNull(message = "Phone number is mandatory.")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone number is invalid.")
    private String phoneNumber;

    @Column(name = "gender")
    @NotNull(message = "Gender is mandatory.")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(mappedBy = "userDetails", fetch = FetchType.EAGER)
    private User user;
}
