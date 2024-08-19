package com.deployblitz.backend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GitDeployUpdateBranchRequestDto(

        @NotBlank(message = "Branch name can't be null")
        @Size(min = 1, max = 100, message = "Branch name size can be lower than 100 characters")
        String branch,

        @NotBlank(message = "Name can't be null")
        @Size(min = 1, max = 100, message = "Name size can be lower than 100 characters")
        String appName) {
}
