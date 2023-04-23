package com.uxui.carwash.service.security;

import com.uxui.carwash.error.ErrorMessage;
import com.uxui.carwash.error.exception.ForbiddenActionException;
import com.uxui.carwash.model.security.Authority;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails getCurrentUserPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new ForbiddenActionException(ErrorMessage.FORBIDDEN);
        }
        return (UserDetails) principal;
    }

    public boolean hasAuthority(String authority) {
        return getCurrentUserPrincipal().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch((auth) -> auth.equals(authority));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("Username: " + username));

        log.info("Loaded user: {}", user.getEmail());

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), user.getEnabled(), user.getAccountNotExpired(),
                user.getCredentialsNotExpired(), user.getAccountNotLocked(), getAuthorities(user.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Authority> authorities) {
        if (authorities == null) {
            return new HashSet<>();
        } else if (authorities.size() == 0) {
            return new HashSet<>();
        }
        else{
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
    }
}
