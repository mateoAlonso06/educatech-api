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
    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {
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
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponseDTO);
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return DTO de respuesta del usuario.
     */
    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = this.getUserEntityById(id);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id             ID del usuario a actualizar.
     * @param userRequestDTO DTO con los datos actualizados del usuario.
     * @return DTO de respuesta del usuario actualizado.
     */
    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User existingUser = this.getUserEntityById(id);

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
    public UserResponseDTO getUserByEmail(String email) {
        return userMapper.toResponseDTO(userRepository.getUserByEmail((email)));
    }

    /**
     * Método auxiliar para obtener una entidad de usuario por su ID.
     * Lanza una excepción si no se encuentra.
     *
     * @param id ID del usuario.
     * @return Entidad de usuario.
     */
    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
