package com.uxui.carwash.controller;

import com.uxui.carwash.model.Car;
import com.uxui.carwash.model.CarType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get create car form - success")
    @WithMockUser(username = "client1@carwash.com", password = "12345", roles = "CLIENT")
    void getForm_success() throws Exception {
        mockMvc.perform(get("/cars/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("car-form"));
    }

    @Test
    @DisplayName("Create car - success")
    @WithMockUser(username = "client1@carwash.com", password = "12345", roles = "CLIENT")
    void create_success() throws Exception {
        mockMvc.perform(post("/cars").flashAttr("car", createCar()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/cars"));
    }

    @Test
    @DisplayName("Get car by id - success")
    @WithMockUser(username = "admin@carwash.com", password = "12345", roles = "ADMIN")
    void getById_success() throws Exception {
        mockMvc.perform(get("/cars/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("car-info"));
    }

    @Test
    @DisplayName("Get all cars - success")
    @WithMockUser(username = "admin@carwash.com", password = "12345", roles = "ADMIN")
    void getAll_success() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"));
    }

    private Car createCar() {
        Car car = new Car();
        car.setType(CarType.VAN);
        car.setLicensePlate("test-car");
        return car;
    }
}