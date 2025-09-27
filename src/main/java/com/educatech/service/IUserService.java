package com.educatech.service;

import com.educatech.dto.request.UserRequestDTO;
import com.educatech.dto.response.UserResponseDTO;
import com.educatech.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    UserResponseDTO saveUser(UserRequestDTO userRequestDTO);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);

    void deleteUser(Long id);

    List<UserResponseDTO> getUsersByRole(Role role);

    UserResponseDTO getUserByEmail(String email);
}
