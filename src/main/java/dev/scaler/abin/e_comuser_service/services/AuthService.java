package dev.scaler.abin.e_comuser_service.services;

import dev.scaler.abin.e_comuser_service.dtos.UserDto;
import dev.scaler.abin.e_comuser_service.models.Role;
import dev.scaler.abin.e_comuser_service.models.Session;
import dev.scaler.abin.e_comuser_service.models.SessionStatus;
import dev.scaler.abin.e_comuser_service.models.User;
import dev.scaler.abin.e_comuser_service.repositories.SessionRepository;
import dev.scaler.abin.e_comuser_service.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class AuthService implements IAuthService {

    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey secretKey;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder, SecretKey secretKey)  {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.secretKey = secretKey;
    }


    //should we add t
    @Override
    public ResponseEntity<UserDto> login(String email, String password, String token) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty())  return null;

        User user =  userOptional.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
//        String token = RandomStringUtils.randomAlphanumeric(32);
        MacAlgorithm alg = Jwts.SIG.HS256;

//        String message = "{\n" +
//                "   \"email\": \"naman@scaler.com\",\n" +
//                "   \"roles\": [\n" +
//                "      \"mentor\",\n" +
//                "      \"ta\"\n" +
//                "   ],\n" +
//                "   \"expirationDate\": \"23rdJuly2028\"\n" +
//                "}";
//      byte[] content = message.getBytes(StandardCharsets.UTF_8);

//      String token = Jwts.builder().content(content).compact();

        Map<String,Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("userId",user.getId());
        jsonForJwt.put("roles",user.getRoles());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiredAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));
        token = Jwts.builder()
                .claims(jsonForJwt)
                .signWith(secretKey,alg)
                .compact();
        Jws<Claims> payload = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        Claims claims = payload.getPayload();
        log.info("Claims: {}", claims);

        Session session = new Session();
        session.setStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto=UserDto.from(user);

        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());

        ResponseCookie cookie = ResponseCookie.from("auth-token",session.getToken()).build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
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
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    @Override
    public SessionStatus validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty())  return SessionStatus.ENDED;

        Session session = sessionOptional.get();

        if (!session.getStatus().equals(SessionStatus.ACTIVE)) {
            return SessionStatus.ENDED;
        }
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Jws<Claims> claimsJws = jwtParser
                .parseSignedClaims(token);

        Long tokenUserId = Long.valueOf(claimsJws.getPayload().get("userId").toString()) ;
        List<Role> roles = (List<Role>) claimsJws.getPayload().get("roles");
        Date createdAt = new Date(Long.valueOf(claimsJws.getPayload().get("createdAt").toString()));
        Long todayMinus1Epoch = LocalDate.now().minusDays(1).toEpochDay();
        log.info("todayMinus1Epoch: {}", todayMinus1Epoch);
        if (createdAt.before(new Date())) {
            return SessionStatus.ENDED;
        }
        return SessionStatus.ACTIVE;
    }
}
