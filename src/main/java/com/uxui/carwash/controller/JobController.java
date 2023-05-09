package com.uxui.carwash.controller;

import com.uxui.carwash.error.exception.AbstractApiException;
import com.uxui.carwash.model.Job;
import com.uxui.carwash.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public String create(@Valid @ModelAttribute(name = "service") Job job, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            return "job-form";
        }

        try {
            jobService.create(job);
        } catch (AbstractApiException e) {
            attr.addFlashAttribute("service", job);
            attr.addFlashAttribute("api_error", e.getMessage());
            return "redirect:/jobs/form";
        }
        return "redirect:/jobs";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long id, @Valid @ModelAttribute(name = "service") Job job, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            return "update-job-form";
        }

        try {
            jobService.update(id, job);
        } catch (AbstractApiException e) {
            attr.addFlashAttribute("service", job);
            attr.addFlashAttribute("api_error", e.getMessage());
            return "redirect:/jobs/form/" + id;
        }
        return "redirect:/jobs";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        jobService.deleteById(id);
        return "redirect:/jobs";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("jobs", jobService.getAll());
        return "jobs";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("job", jobService.getById(id));
        return "job-info";
    }

    @GetMapping("/form")
    public String jobForm(Model model) {
        if (!model.containsAttribute("service")) {
            model.addAttribute("service", new Job());
        }
        return "job-form";
    }

    @GetMapping("/form/{id}")
    public String updateJobForm(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("service")) {
            model.addAttribute("service", jobService.getById(id));
        }
        return "update-job-form";
    }
}
