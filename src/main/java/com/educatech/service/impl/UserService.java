package com.educatech.service.impl;

import com.educatech.dto.request.UserRequestDTO;
import com.educatech.dto.response.UserResponseDTO;
import com.educatech.entity.User;
import com.educatech.enums.Role;
import com.educatech.exception.UserNotFoundException;
import com.educatech.mapper.UserMapper;
import com.educatech.repository.IUserRepository;
import com.educatech.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Guarda un nuevo usuario.
     *
     * @param userRequestDTO DTO de solicitud de usuario.
     * @return DTO de respuesta del usuario guardado.
     */
    @Override
    @Transactional
    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {
        String email = userRequestDTO.getEmail();
        if (userRepository.getUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }
        User userToSave = userMapper.toEntity(userRequestDTO);
        User savedUser = userRepository.save(userToSave);
        return userMapper.toResponseDTO(savedUser);
    }

    /**
     * Obtiene todos los usuarios con paginación.
     *
     * @param pageable Parámetros de paginación.
     * @return Página de DTOs de respuesta de usuarios.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponseDTO);
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return DTO de respuesta del usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + userId);
        }
        User user = this.getUserEntityById(userId);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param userId             ID del usuario a actualizar.
     * @param userRequestDTO DTO con los datos actualizados del usuario.
     * @return DTO de respuesta del usuario actualizado.
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + userId);
        }

        // comprueba que el usuario exista
        User existingUser = this.getUserEntityById(userId);

        String email = userRequestDTO.getEmail();
        String emailExisting = existingUser.getEmail();

        // si el email es distinto al existente, comprueba que no esté en uso
        if (!email.equals(emailExisting) && userRepository.getUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        existingUser.setFirstName(userRequestDTO.getFirstname());
        existingUser.setLastName(userRequestDTO.getLastName());
        existingUser.setEmail(userRequestDTO.getEmail());
        existingUser.setPassword(userRequestDTO.getPassword());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Elimina un usuario dado su ID.
     *
     * @param id ID del usuario a eliminar.
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User userToDelete = this.getUserEntityById(id);
        userRepository.delete(userToDelete);
    }

    /**
     * Obtiene los usuarios por un rol específico.
     *
     * @param role Rol a filtrar.
     * @return Lista de DTOs de respuesta de usuarios.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(Role role) {
        return userRepository.getUsersByRole(role)
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email Email del usuario a buscar.
     * @return DTO de respuesta del usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toResponseDTO(user);
    }

    // Método privado para obtener la entidad User por ID con manejo de excepción
    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
