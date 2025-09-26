package com.educatech.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRequestDTO {
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name must be at most 100 characters")
    private final String firstname;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    private final String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private final String email;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, max = 60, message = "Password must be between 8 and 60 characters long")
    private final String password;
}
