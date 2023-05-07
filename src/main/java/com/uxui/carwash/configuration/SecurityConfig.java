package com.uxui.carwash.configuration;

import com.uxui.carwash.service.security.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JpaUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // jobs
                .antMatchers("static/**").permitAll()
                .antMatchers("/jobs/form/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/jobs").permitAll()
                .antMatchers(HttpMethod.GET, "/jobs/*").permitAll()
                .antMatchers(HttpMethod.POST, "/jobs").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/jobs/*").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/jobs/*").hasAnyRole("ADMIN")
                // cars
                .antMatchers("/cars/form/**").hasAnyRole("CLIENT")
                .antMatchers(HttpMethod.GET, "/cars", "/cars/*").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.POST, "/cars").hasAnyRole("CLIENT")
                .antMatchers(HttpMethod.DELETE, "/cars/*").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/cars/*").hasAnyRole("CLIENT")
                // employees
                .antMatchers("/employees/**").hasAnyRole("ADMIN")
                // appointments
                .antMatchers("/appointments/form/**").hasAnyRole("CLIENT")
                .antMatchers(HttpMethod.GET, "/appointments", "/appointments/*").hasAnyRole("CLIENT", "ADMIN", "EMPLOYEE")
                .antMatchers(HttpMethod.POST, "/appointments").hasAnyRole("CLIENT")
                .antMatchers(HttpMethod.DELETE, "/appointments/*").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/appointments/*").hasAnyRole("CLIENT")
                // users
                .antMatchers(HttpMethod.GET, "/users/form").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/users/*", "/users/current").hasAnyRole("CLIENT", "ADMIN", "EMPLOYEE")
                .antMatchers(HttpMethod.GET, "/users").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/users/*").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/users/form/*").hasAnyRole("CLIENT", "ADMIN")
                .and()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
                .formLogin().loginPage("/login-form")
                .loginProcessingUrl("/auth")
                .permitAll()
                .and()
                .logout().permitAll()
                .logoutUrl("/perform-logout")
                .logoutSuccessUrl("/index")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied");
    }
}
