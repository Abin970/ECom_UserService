package dev.scaler.abin.e_comuser_service.services;

import dev.scaler.abin.e_comuser_service.dtos.UserDto;
import dev.scaler.abin.e_comuser_service.models.Session;
import dev.scaler.abin.e_comuser_service.models.SessionStatus;
import dev.scaler.abin.e_comuser_service.models.User;
import dev.scaler.abin.e_comuser_service.repositories.SessionRepository;
import dev.scaler.abin.e_comuser_service.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }


    @Override
    public ResponseEntity<UserDto> login(String email, String password) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty())  return null;

        User user =  userOptional.get();

        if(!user.getPassword().equals(password))
            return null;
        String token = RandomStringUtils.randomAlphanumeric(32);
        Session session = new Session();
        session.setStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);


        UserDto userDto=UserDto.from(user);

        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:"+session.getToken());
        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);
        return response;
    }

    @Override
    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty())  return null;
        Session session = sessionOptional.get();

        session.setStatus(SessionStatus.ENDED);
        sessionRepository.save(session);


        return ResponseEntity.ok().build();
    }

    @Override
    public UserDto signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    @Override
    public SessionStatus validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if(sessionOptional.isEmpty())  return null;


        return SessionStatus.ACTIVE;
    }
}
