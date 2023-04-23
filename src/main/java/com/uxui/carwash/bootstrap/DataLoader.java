package com.uxui.carwash.bootstrap;

import com.uxui.carwash.model.Gender;
import com.uxui.carwash.model.UserDetails;
import com.uxui.carwash.model.security.Authority;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.security.AuthorityRepository;
import com.uxui.carwash.repository.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile({"h2", "mysql"})
public class DataLoader implements CommandLineRunner {

    private AuthorityRepository authorityRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadUserData();
    }

    private void loadUserData() {
        if (userRepository.count() == 0){
            Authority adminRole = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority guestRole = authorityRepository.save(Authority.builder().role("ROLE_GUEST").build());

            UserDetails adminDetails = UserDetails.builder()
                    .firstName("admin")
                    .lastName("carwash")
                    .phoneNumber("0761111111")
                    .gender(Gender.MALE)
                    .build();

            User admin = User.builder()
                    .email("admin@carwash.com")
                    .password(passwordEncoder.encode("12345"))
                    .authority(adminRole)
                    .userDetails(adminDetails)
                    .build();

            UserDetails guestDetails = UserDetails.builder()
                    .firstName("guest")
                    .lastName("carwash")
                    .phoneNumber("0751111111")
                    .gender(Gender.MALE)
                    .build();

            User guest = User.builder()
                    .email("guest@carwash.com")
                    .password(passwordEncoder.encode("12345"))
                    .authority(guestRole)
                    .userDetails(guestDetails)
                    .build();

            userRepository.save(admin);
            userRepository.save(guest);
        }
    }
}
