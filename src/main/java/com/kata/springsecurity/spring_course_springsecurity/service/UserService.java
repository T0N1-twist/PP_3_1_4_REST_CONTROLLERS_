package com.kata.springsecurity.spring_course_springsecurity.service;

import com.kata.springsecurity.spring_course_springsecurity.model.User;

import java.util.List;


public interface UserService {

    public List<User> getAllUsers();

     User getUserById(Long id);

    void saveUser(User user);

     void updateUser(User user);

     void deleteUser(Long id);

     User findByEmail(String email);

     boolean isExistByUsername(String username);

     User findWithRolesByUsername(String username);


}
