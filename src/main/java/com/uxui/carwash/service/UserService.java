package com.uxui.carwash.service;

import com.uxui.carwash.error.ErrorMessage;
import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ForbiddenActionException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.security.Authority;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.security.AuthorityRepository;
import com.uxui.carwash.repository.security.UserRepository;
import com.uxui.carwash.service.security.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService  {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JpaUserDetailsService jpaUserDetailsService;

    public User create(User user, String role) {
        checkUserNotExisting(user);

        Authority authority = authorityRepository.findByRole(role);
        user.setAuthorities(Collections.singleton(authority));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User update(Long id, User user) {
        User existingUser = getById(id);
        if (!existingUser.getEmail().equals(user.getEmail())) {
            checkEmailNotExisting(user);
        }
        if (!existingUser.getUserDetails().getPhoneNumber().equals(user.getUserDetails().getPhoneNumber())) {
            checkPhoneNumberNotExisting(user);
        }
        boolean isAdmin = jpaUserDetailsService.hasAuthority("ROLE_ADMIN");
        if (!isAdmin && !existingUser.getEmail().equals(jpaUserDetailsService.getCurrentUserPrincipal().getUsername())) {
            throw new ForbiddenActionException(ErrorMessage.FORBIDDEN);
        }
        copyValues(existingUser, user);

        return userRepository.save(existingUser);
    }

    public User getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND, "user", id));
        boolean isAdmin = jpaUserDetailsService.hasAuthority("ROLE_ADMIN");
        if (!isAdmin && !user.getEmail().equals(jpaUserDetailsService.getCurrentUserPrincipal().getUsername())) {
            throw new ForbiddenActionException(ErrorMessage.FORBIDDEN);
        }
        return user;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND, "user", email));
    }

    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        User user = getById(id);
        userRepository.delete(user);

        if (user.getEmail().equals(jpaUserDetailsService.getCurrentUserPrincipal().getUsername())) {
            SecurityContextHolder.clearContext(); // force logout
        }
    }

    private void checkUserNotExisting(User user) {
        if (userRepository.existsByEmailOrPhoneNumber(user.getEmail(), user.getUserDetails().getPhoneNumber())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "user", "email or phone number");
        }
    }

    private void checkEmailNotExisting(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "user", "email");
        }
    }

    private void checkPhoneNumberNotExisting(User user) {
        if (userRepository.existsByPhoneNumber(user.getUserDetails().getPhoneNumber())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "user", "phone number");
        }
    }

    private void copyValues(User to, User from) {
        to.getUserDetails().setFirstName(from.getUserDetails().getFirstName());
        to.getUserDetails().setLastName(from.getUserDetails().getLastName());
        to.getUserDetails().setGender(from.getUserDetails().getGender());
        to.getUserDetails().setPhoneNumber(from.getUserDetails().getPhoneNumber());
        to.setEmail(from.getEmail());
        if (!to.getPassword().equals(from.getPassword())) {
            to.setPassword(bCryptPasswordEncoder.encode(from.getPassword()));
        }
    }
}
