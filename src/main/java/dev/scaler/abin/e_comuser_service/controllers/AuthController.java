package dev.scaler.abin.e_comuser_service.controllers;

import dev.scaler.abin.e_comuser_service.dtos.LoginRequestDto;
import dev.scaler.abin.e_comuser_service.dtos.LogoutRequestDto;
import dev.scaler.abin.e_comuser_service.dtos.UserDto;
import dev.scaler.abin.e_comuser_service.services.IAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        return authService.logout(logoutRequestDto.getToken(), logoutRequestDto.getUserId());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody UserDto userDto) {

    }

}
