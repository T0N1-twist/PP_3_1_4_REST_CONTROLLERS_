package com.kata.springsecurity.spring_course_springsecurity.controller;

import com.kata.springsecurity.spring_course_springsecurity.model.User;
import com.kata.springsecurity.spring_course_springsecurity.security.UserDetailsCustomClass;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {


    @GetMapping
    public String showUserPage() {
        return "user";
    }

    @GetMapping(value = "/current", produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        UserDetailsCustomClass details = (UserDetailsCustomClass) authentication.getPrincipal();
        return ResponseEntity.ok(details.getUser());
    }
}