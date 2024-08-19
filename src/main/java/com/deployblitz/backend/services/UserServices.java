package com.deployblitz.backend.services;

import com.deployblitz.backend.domain.dto.request.UserRegisterRequestDto;
import com.deployblitz.backend.domain.dto.response.UserFindTokenResponseDto;
import com.deployblitz.backend.utils.HttpResponse;

public interface UserServices {
    HttpResponse<?> register(UserRegisterRequestDto userRegisterRequestDto);

    HttpResponse<UserFindTokenResponseDto> findTokenByUser(String username);
}
