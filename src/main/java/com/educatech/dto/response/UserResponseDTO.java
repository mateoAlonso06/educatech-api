package com.educatech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResponseDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String role;
}
