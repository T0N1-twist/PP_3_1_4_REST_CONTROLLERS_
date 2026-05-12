package com.kata.springsecurity.spring_course_springsecurity.configs;

public class HashGenerator {
    public static void main(String[] args) {
        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("user"));

   }
}
