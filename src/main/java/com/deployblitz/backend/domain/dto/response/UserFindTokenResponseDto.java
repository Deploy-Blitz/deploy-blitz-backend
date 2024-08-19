package com.deployblitz.backend.domain.dto.response;

import com.deployblitz.backend.domain.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFindTokenResponseDto {
    private String token;

    public UserFindTokenResponseDto(UserEntity user) {
        this.token = user.getToken();
    }
}
