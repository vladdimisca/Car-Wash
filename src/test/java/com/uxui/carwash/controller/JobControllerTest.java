package com.uxui.carwash.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get job by id - success")
    void getById_success() throws Exception {
        mockMvc.perform(get("/jobs/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("job-info"));
    }

    @Test
    @DisplayName("Get all jobs - success")
    void getAll_success() throws Exception {
        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs"));
    }
}