package com.uxui.carwash.controller;

import com.uxui.carwash.error.exception.AbstractApiException;
import com.uxui.carwash.model.Car;
import com.uxui.carwash.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public String create(@Valid @ModelAttribute Car car, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            return "car-form";
        }

        try {
            carService.create(car);
        } catch (AbstractApiException e) {
            attr.addFlashAttribute("car", car);
            attr.addFlashAttribute("api_error", e.getMessage());
            return "redirect:/cars/form";
        }
        return "redirect:/cars";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long id, @Valid @ModelAttribute Car car, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            return "update-car-form";
        }

        try {
            carService.update(id, car);
        } catch (AbstractApiException e) {
            attr.addFlashAttribute("car", car);
            attr.addFlashAttribute("api_error", e.getMessage());
            return "redirect:/cars/form/" + id;
        }
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
        if (!model.containsAttribute("car")) {
            model.addAttribute("car", new Car());
        }
        return "car-form";
    }

    @GetMapping("/form/{id}")
    public String updateCarForm(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("car")) {
            model.addAttribute("car", carService.getById(id));
        }
        return "update-car-form";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        carService.deleteById(id);
        return "redirect:/cars";
    }
}
