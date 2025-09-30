package com.educatech.repository;

import com.educatech.entity.User;
import com.educatech.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByEmail(String email);

    List<User> getUsersByRole(Role role);
}
