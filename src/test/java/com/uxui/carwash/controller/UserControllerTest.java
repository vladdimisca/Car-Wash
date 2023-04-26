package com.uxui.carwash.controller;

import com.uxui.carwash.model.Gender;
import com.uxui.carwash.model.UserDetails;
import com.uxui.carwash.model.security.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    void showRegisterPage_success() throws Exception {
        mockMvc.perform(get("/users/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithAnonymousUser
    void register_success() throws Exception {
        User user = createUser();

        mockMvc.perform(post("/users")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login-form"));
    }

    @Test
    @WithAnonymousUser
    void register_notValidFields_failure() throws Exception {
        User user = createUser();
        user.setPassword("");

        mockMvc.perform(post("/users")
                        .flashAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("register-form"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Get current user - success")
    @WithMockUser(username = "client1@carwash.com", password = "12345", roles = "CLIENT")
    void getCurrentUser_success() throws Exception {
        mockMvc.perform(get("/users/current"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-info"));
    }

    @Test
    @DisplayName("Get current user - unauthenticated - failure")
    @WithAnonymousUser
    void getCurrentUser_unauthenticated_failure() throws Exception {
        mockMvc.perform(get("/users/current"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login-form"));
    }

    private User createUser() {
        UserDetails userDetails = UserDetails.builder()
                .firstName("testfn")
                .lastName("testln")
                .phoneNumber("0762323231")
                .gender(Gender.MALE)
                .build();

        return User.builder()
                .email("test@carwash.com")
                .password("random12345")
                .userDetails(userDetails)
                .build();
    }
}