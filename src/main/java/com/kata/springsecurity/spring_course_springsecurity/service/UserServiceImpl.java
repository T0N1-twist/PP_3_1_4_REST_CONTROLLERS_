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
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
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
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Integer> roleIds = user.getRoles().stream()
                    .map(r -> r.getId())
                    .toList();
            user.setRoles(new java.util.HashSet<>(roleService.getRolesByIds(roleIds)));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, User user) {
        // setId перенесён сюда из контроллера (по замечанию ментора)
        user.setId(id);

        User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(user.getUsername());
        existing.setLastName(user.getLastName());
        existing.setAge(user.getAge());
        existing.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null) {
            List<Integer> roleIds = user.getRoles().stream()
                    .map(r -> r.getId())
                    .toList();
            existing.setRoles(new java.util.HashSet<>(roleService.getRolesByIds(roleIds)));
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
