package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.domain.dto.request.UserRegisterRequestDto;
import com.deployblitz.backend.domain.dto.response.UserFindTokenResponseDto;
import com.deployblitz.backend.domain.entities.UserEntity;
import com.deployblitz.backend.repository.UserRepository;
import com.deployblitz.backend.services.UserServices;
import com.deployblitz.backend.utils.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserServices {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public HttpResponse<?> register(UserRegisterRequestDto userRegisterRequestDto) {
        var response = userRepository.save(new UserEntity(userRegisterRequestDto));
        return new HttpResponse<>(HttpStatus.CREATED);
    }

    @Override
    public HttpResponse<UserFindTokenResponseDto> findTokenByUser(String username) {
        var response = userRepository.findByUsername(username);
        if (response == null) {
            return new HttpResponse<>(HttpStatus.NOT_FOUND);
        }
        return new HttpResponse<>(HttpStatus.OK, new UserFindTokenResponseDto(response));
    }
}
