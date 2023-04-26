package com.uxui.carwash.controller;

import com.uxui.carwash.model.Car;
import com.uxui.carwash.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public String create(@Valid @ModelAttribute Car car, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "car-form";
        }

        carService.create(car);
        return "redirect:/cars";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("cars", carService.getAll());
        return "cars";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("car", carService.getById(id));
        return "car-info";
    }

    @GetMapping("/form")
    public String carForm(Model model) {
        model.addAttribute("car", new Car());
        return "car-form";
    }
}
