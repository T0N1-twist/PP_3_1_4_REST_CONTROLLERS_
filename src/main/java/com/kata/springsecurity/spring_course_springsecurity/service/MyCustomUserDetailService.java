package com.kata.springsecurity.spring_course_springsecurity.service;


import com.kata.springsecurity.spring_course_springsecurity.security.UserDetailsCustomClass;
import com.kata.springsecurity.spring_course_springsecurity.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyCustomUserDetailService implements UserDetailsService {

    private UserService userService;

    public MyCustomUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userService.findByEmail(email);

        return new UserDetailsCustomClass(user);
    }


}
