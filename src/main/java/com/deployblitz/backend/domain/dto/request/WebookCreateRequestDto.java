package com.deployblitz.backend.domain.dto.request;

public record WebookCreateRequestDto(
        String name,
        String branch,
        String gitToken,
        String gitHttpsUri
) {
}
