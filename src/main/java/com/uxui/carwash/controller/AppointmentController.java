package com.uxui.carwash.controller;

import com.uxui.carwash.error.exception.AbstractApiException;
import com.uxui.carwash.model.Appointment;
import com.uxui.carwash.model.Car;
import com.uxui.carwash.model.Job;
import com.uxui.carwash.service.AppointmentService;
import com.uxui.carwash.service.CarService;
import com.uxui.carwash.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CarService carService;
    private final JobService jobService;

    @PostMapping
    public String create(@Valid @ModelAttribute Appointment appointment, BindingResult bindingResult, Model model, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("cars", carService.getAll());
            model.addAttribute("jobs", jobService.getAll());
            model.addAttribute("carTypes", getCarTypes(carService.getAll()));
            model.addAttribute("jobsCarTypes", getJobsCarTypes(jobService.getAll()));
            return "appointment-form";
        }

        try {
            appointmentService.create(appointment);
        } catch (AbstractApiException e) {
            attr.addFlashAttribute("appointment", appointment);
            attr.addFlashAttribute("api_error", e.getMessage());
            return "redirect:/appointments/form";
        }
        return "redirect:/appointments";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("appointments", appointmentService.getAll());
        return "appointments";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getById(id));
        return "appointment-info";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        appointmentService.deleteById(id);
        return "redirect:/appointments";
    }

    @GetMapping("/form")
    public String appointmentForm(Model model) {
        if (!model.containsAttribute("appointment")) {
            model.addAttribute("appointment", new Appointment());
        }
        if (!model.containsAttribute("cars")) {
            model.addAttribute("cars", carService.getAll());
        }
        if (!model.containsAttribute("jobs")) {
            model.addAttribute("jobs", jobService.getAll());
        }
        if (!model.containsAttribute("carTypes")) {
            model.addAttribute("carTypes", getCarTypes(carService.getAll()));
        }
        if (!model.containsAttribute("jobsCarTypes")) {
            model.addAttribute("jobsCarTypes", getJobsCarTypes(jobService.getAll()));
        }
        return "appointment-form";
    }

    private List<Car> getCarTypes(List<Car> cars) {
        return cars.stream()
                .map(car -> Car.builder()
                        .id(car.getId())
                        .type(car.getType())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Job> getJobsCarTypes(List<Job> jobs) {
        return jobs.stream()
                .map(job -> Job.builder()
                        .id(job.getId())
                        .carType(job.getCarType())
                        .build())
                .collect(Collectors.toList());
    }
}
