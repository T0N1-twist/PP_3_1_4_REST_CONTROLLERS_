package com.kata.springsecurity.spring_course_springsecurity.service;

import com.kata.springsecurity.spring_course_springsecurity.repository.UserRepository;
import com.kata.springsecurity.spring_course_springsecurity.model.User;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

   public  UserServiceImpl(UserRepository userRepository) {
       this.userRepository = userRepository;
   }

  @Override
  public List<User> getAllUsers() {
     return userRepository.findAll();
  }

  @Override
    public User getUserById(Long id) {
     return userRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("User not found"));
  }

  @Override
    public void saveUser(User user) {
       userRepository.save(user);

  }

  @Override
    public void updateUser(User user) {
userRepository.save(user);
  }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found by email: " + email));
    }


    @Override
    public boolean isExistByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findWithRolesByUsername(String username) {
        return userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new RuntimeException("User by this username is not found"));
    }



}
