package com.uxui.carwash.service;

import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.UserDetails;
import com.uxui.carwash.model.security.Authority;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.security.AuthorityRepository;
import com.uxui.carwash.repository.security.UserRepository;
import com.uxui.carwash.service.security.JpaUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long ID = 1L;
    private static final String USER_EMAIL = "test";
    private static final String ENCODED_PASS = "encoded";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JpaUserDetailsService jpaUserDetailsService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Create user - success")
    void create_success() {
        User user = getUser();
        User savedUser = getSavedUser();
        Authority authority = getAuthority();

        when(userRepository.existsByEmailOrPhoneNumber(user.getEmail(), user.getUserDetails().getPhoneNumber())).thenReturn(false);
        when(authorityRepository.findByRole("ROLE_CLIENT")).thenReturn(authority);
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn(ENCODED_PASS);
        when(userRepository.save(user)).thenReturn(savedUser);

        User resultedUser = userService.create(user, "ROLE_CLIENT");

        assertNotNull(resultedUser);
        assertEquals(savedUser.getId(), resultedUser.getId());
        assertEquals(savedUser.getEmail(), resultedUser.getEmail());
        assertEquals(savedUser.getPassword(), ENCODED_PASS);
        verify(userRepository, times(1))
                .existsByEmailOrPhoneNumber(user.getEmail(), user.getUserDetails().getPhoneNumber());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Create user - existing email or phone number - success")
    void create_existingEmailOrPhoneNumber_failure() {
        User user = getUser();

        when(userRepository.existsByEmailOrPhoneNumber(user.getEmail(), user.getUserDetails().getPhoneNumber())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(user, "ROLE_CLIENT"));
        verify(userRepository, times(1))
                .existsByEmailOrPhoneNumber(user.getEmail(), user.getUserDetails().getPhoneNumber());
    }

    @Test
    @DisplayName("Get user by id - success")
    void getById_success() {
        User user = getSavedUser();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        User resultedUser = userService.getById(ID);

        assertNotNull(resultedUser);
        assertEquals(user.getId(), resultedUser.getId());
        assertEquals(user.getEmail(), resultedUser.getEmail());
        verify(userRepository, times(1)).findById(ID);
        verify(jpaUserDetailsService, times(1)).hasAuthority("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Get user by id - not found - failure")
    void getById_notFound_failure() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(ID));
        verify(userRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Get user by email - success")
    void getByEmail_success() {
        User user = getSavedUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User resultedUser = userService.getByEmail(user.getEmail());

        assertNotNull(resultedUser);
        assertEquals(user.getId(), resultedUser.getId());
        assertEquals(user.getEmail(), resultedUser.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Get user by id - not found - failure")
    void getByEmail_notFound_failure() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getByEmail(USER_EMAIL));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    @DisplayName("Get all users - success")
    void getAll_success() {
        Pageable pageable = PageRequest.of(1, 5, Sort.by("email").descending());

        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(getSavedUser())));

        Page<User> usersPage = userService.getAll(pageable);

        assertNotNull(usersPage);
        assertEquals(1, usersPage.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Update user by id - success")
    void update_success() {
        User user = getSavedUser();
        User updatedUser = getSavedUser();
        updatedUser.setEmail("updated email");
        updatedUser.getUserDetails().setPhoneNumber("updated phone number");

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(true);
        when(userRepository.existsByPhoneNumber(updatedUser.getUserDetails().getPhoneNumber())).thenReturn(false);
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(updatedUser);

        User resultedUser = userService.update(ID, updatedUser);

        assertNotNull(resultedUser);
        assertEquals(updatedUser.getId(), resultedUser.getId());
        assertEquals(updatedUser.getEmail(), resultedUser.getEmail());
        verify(userRepository, times(1)).findById(ID);
        verify(jpaUserDetailsService, times(2)).hasAuthority("ROLE_ADMIN");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Delete user by id - success")
    void delete_success() {
        User user = getSavedUser();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(true);
        when(jpaUserDetailsService.getCurrentUserPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User(USER_EMAIL, "pass", new HashSet<>()));

        userService.deleteById(ID);

        verify(userRepository, times(1)).findById(ID);
        verify(jpaUserDetailsService, times(1)).hasAuthority("ROLE_ADMIN");
        verify(userRepository, times(1)).delete(user);
    }

    private User getUser() {
        UserDetails userDetails = UserDetails.builder()
                .phoneNumber("test phone number")
                .firstName("test")
                .lastName("test")
                .build();

        return User.builder()
                .email(USER_EMAIL)
                .password("pass")
                .userDetails(userDetails)
                .build();
    }

    private User getSavedUser() {
        User user = getUser();
        user.setId(ID);
        user.setPassword(ENCODED_PASS);

        return user;
    }

    private Authority getAuthority() {
        Authority authority = new Authority();
        authority.setRole("ROLE_ADMIN");

        return authority;
    }
}
