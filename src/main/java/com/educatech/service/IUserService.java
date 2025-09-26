package com.educatech.service;

import com.educatech.entity.User;
import com.educatech.enums.Role;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User saveUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User updateUser(User user);
    void deleteUser(Long id);
    List<User> getUsersByRole(Role role);
    Optional<User> getUserByEmail(String email);
}
