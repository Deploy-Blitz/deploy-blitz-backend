package com.deployblitz.backend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WebHookCreateRequestDto(
        @NotBlank(message = "Name can't be null")
        @Size(min = 1, max = 100, message = "Name size can be lower than 100 characters")
        String name,

        @NotBlank(message = "Branch name can't be null")
        @Size(min = 1, max = 100, message = "Branch name size can be lower than 100 characters")
        String branch,

        String gitToken,

        @NotBlank(message = "Git Repo Url can't be null")
        @Size(min = 1, max = 100, message = "Git Repo Url name size can be lower than 100 characters")
        @Pattern(
                regexp = "^(https?://.*\\.git)$",
                message = "Git Repo Url must be a valid URL and end with .git"
        )
        String gitHttpsUri
) {
}
