package com.kata.springsecurity.spring_course_springsecurity.service;

import com.kata.springsecurity.spring_course_springsecurity.security.UserDetailsCustomClass;
import com.kata.springsecurity.spring_course_springsecurity.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MyCustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    public MyCustomUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        return new UserDetailsCustomClass(user);
    }

}
