package com.deployblitz.backend.controller;

import com.deployblitz.backend.domain.dto.request.UserRegisterRequestDto;
import com.deployblitz.backend.services.UserServices;
import com.deployblitz.backend.utils.HttpResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController(value = "/user")
public class UserController {
    private final UserServices userService;

    public UserController(UserServices userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public HttpResponse<?> register(@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) {
        return userService.register(userRegisterRequestDto);
    }

    @GetMapping("/valid/user/{username}")
    public HttpResponse<?> validUser(@PathVariable String username) {
        return userService.findTokenByUser(username);
    }
}
