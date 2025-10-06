package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.exceptions.UnsuccessfulLoginException;
import com.onlinestore.BestShop.model.JwtResponse;
import com.onlinestore.BestShop.model.LoginRequest;
import com.onlinestore.BestShop.model.User;
import com.onlinestore.BestShop.persistence.UserRepository;
import com.onlinestore.BestShop.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${spring.jwt.refreshTokenExpiration}")
    private int refreshTokenExpiration;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                             HttpServletResponse response){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        String token = jwtService.generateAccessToken(loginRequest.getEmail());
        String refreshToken = jwtService.generateRefreshToken(loginRequest.getEmail());

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(refreshTokenExpiration);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken){
        if(!jwtService.validateToken(refreshToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String email = jwtService.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user.getEmail());

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.validateToken(token);
    }

    @ExceptionHandler(UnsuccessfulLoginException.class)
    public ResponseEntity<Map<String,String>> handleUserNotFoundException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
