package com.deployblitz.backend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestDto(
        @NotBlank(message = "Username can't be null")
        @Size(min = 1, max = 100, message = "Username size can be lower than 100 characters")
        String username,

        @NotBlank(message = "Token can't be null")
        @Size(min = 1, max = 40, message = "Token size can be lower than 40 characters")
        String token) {
}
