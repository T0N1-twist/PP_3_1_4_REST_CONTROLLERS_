package com.kata.springsecurity.spring_course_springsecurity.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table (name = "players")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name")
    private String username;

    @Column (name = "lastName")
    private String lastName;

    @Column (name = "age")
    private int age;

    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @Column (name = "password")
    private String password;


    public User (String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
            name = "player_roles",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
