package com.kata.springsecurity.spring_course_springsecurity.controller;


import com.kata.springsecurity.spring_course_springsecurity.service.RoleService;
import com.kata.springsecurity.spring_course_springsecurity.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;


    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showAdminPage(Model model, Authentication authentication) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("currentUser", authentication.getName());
        model.addAttribute("currentUserRoles", authentication.getAuthorities());
        return "admin";
    }

    @PostMapping("/new")
    public String createUser(@RequestParam String username,
                             @RequestParam String lastName,
                             @RequestParam Integer age,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(required = false) List<Integer> roleIds) {
        userService.saveUser(username, lastName, age, email, password, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String username,
                             @RequestParam String lastName,
                             @RequestParam Integer age,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) List<Integer> roleIds) {
        userService.updateUser(id, username, lastName, age, email, password, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
