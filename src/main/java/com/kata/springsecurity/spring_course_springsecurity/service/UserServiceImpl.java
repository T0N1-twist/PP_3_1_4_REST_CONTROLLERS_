package com.kata.springsecurity.spring_course_springsecurity.service;

import com.kata.springsecurity.spring_course_springsecurity.repository.UserRepository;
import com.kata.springsecurity.spring_course_springsecurity.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    public  UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder) {
       this.userRepository = userRepository;
       this.passwordEncoder = passwordEncoder;
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
  @Transactional
    public void saveUser(User user) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      userRepository.save(user);
  }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existing = userRepository.findById(user.getId()).orElseThrow();
        existing.setUsername(user.getUsername());
        existing.setLastName(user.getLastName());
        existing.setAge(user.getAge());
        existing.setEmail(user.getEmail());
        existing.setRoles(user.getRoles());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(existing);
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
