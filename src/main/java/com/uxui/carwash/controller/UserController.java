package com.uxui.carwash.controller;

import com.uxui.carwash.error.exception.AbstractApiException;
import com.uxui.carwash.model.UserDetails;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.service.UserService;
import com.uxui.carwash.service.security.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JpaUserDetailsService jpaUserDetailsService;

    @PostMapping
    public String create(@ModelAttribute @Valid User user, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            return "register-form";
        }

        try {
            userService.create(user, "ROLE_CLIENT");
        } catch (AbstractApiException e) {
            attr.addFlashAttribute("user", user);
            attr.addFlashAttribute("api_error", e.getMessage());
            return "redirect:/users/form";
        }
        return "redirect:/login-form";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long id, @Valid @ModelAttribute User user, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            return "update-user-form";
        }

        try {
            userService.update(id, user);
        } catch (AbstractApiException e) {
            attr.addFlashAttribute("user", user);
            attr.addFlashAttribute("api_error", e.getMessage());
            return "redirect:/users/form/" + id;
        }
        return "redirect:/users/current";
    }

    @GetMapping("/form")
    public String userForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (!model.containsAttribute("user")) {
                User user = new User();
                user.setUserDetails(new UserDetails());
                model.addAttribute("user", user);
            } else {
                User user = (User) model.getAttribute("user");
                user.setPassword("");
            }
            return "register-form";
        }
        return "redirect:/index";
    }

    @GetMapping("/form/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", userService.getById(id));
        }
        return "update-user-form";
    }

    @GetMapping("/current")
    public String getCurrentUser(Model model) {
        User user = userService.getByEmail(jpaUserDetailsService.getCurrentUserPrincipal().getUsername());
        model.addAttribute("user", user);
        model.addAttribute("updatable", true);
        return "user-info";
    }
}
