package com.kata.springsecurity.spring_course_springsecurity.controller;

import com.kata.springsecurity.spring_course_springsecurity.model.Role;
import com.kata.springsecurity.spring_course_springsecurity.model.User;
import com.kata.springsecurity.spring_course_springsecurity.service.RoleService;
import com.kata.springsecurity.spring_course_springsecurity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    public String showAdminPage() {
        return "admin";
    }

    @GetMapping(value = "/users", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(value = "/users/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping(value = "/users", produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/users/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping(value = "/users/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/roles", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}