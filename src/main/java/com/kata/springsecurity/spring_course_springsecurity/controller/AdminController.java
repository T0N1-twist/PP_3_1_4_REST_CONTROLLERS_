package com.kata.springsecurity.spring_course_springsecurity.controller;

import com.kata.springsecurity.spring_course_springsecurity.model.Role;
import com.kata.springsecurity.spring_course_springsecurity.service.RoleService;
import com.kata.springsecurity.spring_course_springsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.kata.springsecurity.spring_course_springsecurity.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/generate-hash")
    @ResponseBody
    public String generateHash() {
        return new BCryptPasswordEncoder().encode("admin");
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
        User user = new User();
        user.setUsername(username);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(password);
        if (roleIds != null) {
            Set<Role> roles = new HashSet<>(roleService.getRolesByIds(roleIds));
            user.setRoles(roles);
        }
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String username,
                             @RequestParam String lastName,
                             @RequestParam Integer age,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,  // ← required = false
                             @RequestParam(required = false) List<Integer> roleIds) {
        User user = userService.getUserById(id);
        user.setUsername(username);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);

        // просто передаём пароль как есть (null или plain text)
        // сервис сам проверит isBlank и зашифрует
        user.setPassword(password);

        if (roleIds != null) {
            Set<Role> roles = new HashSet<>(roleService.getRolesByIds(roleIds));
            user.setRoles(roles);
        }
        userService.updateUser(user);
        return "redirect:/admin";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
