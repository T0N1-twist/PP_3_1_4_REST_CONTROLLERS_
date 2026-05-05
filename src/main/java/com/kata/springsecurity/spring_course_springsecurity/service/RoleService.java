package com.kata.springsecurity.spring_course_springsecurity.service;

import com.kata.springsecurity.spring_course_springsecurity.model.Role;

import java.util.List;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleById(int id);

    Role findByName(String name);

    void save(Role role);

    void deleteById(int id);

    List<Role> getRolesByIds(List<Integer> ids);
}
