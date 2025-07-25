package dev.scaler.abin.e_comuser_service.services;

import dev.scaler.abin.e_comuser_service.dtos.UserDto;
import dev.scaler.abin.e_comuser_service.models.SessionStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IAuthService {

    ResponseEntity<UserDto> login(String email, String password);

    ResponseEntity<Void> logout(String token, Long userId);

    UserDto signUp(String email, String password);

    SessionStatus validate(String token, Long userId);
}
