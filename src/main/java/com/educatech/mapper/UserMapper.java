package com.educatech.mapper;

import com.educatech.dto.request.UserRequestDTO;
import com.educatech.dto.response.UserResponseDTO;
import com.educatech.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a User entity to a UserResponseDTO.
     * Maps the role enum to its string representation.
     *
     * @param user The User entity.
     * @return The mapped UserResponseDTO.
     */
    @Mapping(source = "role", target = "role")
    UserResponseDTO toResponseDTO(User user);

    /**
     * Maps a UserRequestDTO to a User entity.
     * 'id' is ignored as it's auto-generated.
     * 'role' is ignored because it will be set in the service layer.
     *
     * @param dto The UserRequestDTO.
     * @return The mapped User entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRequestDTO dto);
}
