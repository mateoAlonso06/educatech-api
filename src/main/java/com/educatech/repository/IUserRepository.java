package com.educatech.repository;

import com.educatech.entity.User;
import com.educatech.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    User getUserByEmail(String email);

    List<User> getUsersByRole(Role role);
}
