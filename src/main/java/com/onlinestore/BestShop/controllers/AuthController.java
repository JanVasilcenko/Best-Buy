package com.onlinestore.BestShop.controllers;

import com.onlinestore.BestShop.exceptions.DuplicateUserException;
import com.onlinestore.BestShop.exceptions.UnsuccessfulLoginException;
import com.onlinestore.BestShop.model.*;
import com.onlinestore.BestShop.services.AuthService;
import com.onlinestore.BestShop.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Value("${spring.jwt.refreshTokenExpiration}")
    private int refreshTokenExpiration;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                             HttpServletResponse response){

        LoginResponse loginResponse = authService.login(loginRequest);

        Cookie cookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(refreshTokenExpiration);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(loginResponse.getAccessToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest){
        userService.registerUser(registerUserRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken){
        String accessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(){
        User currentUser = authService.getCurrentUser();

        if (currentUser == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(currentUser);
    }

    @ExceptionHandler(UnsuccessfulLoginException.class)
    public ResponseEntity<Map<String,String>> handleUserNotFoundException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String,String>> handleDuplicateUser(){
        return ResponseEntity.badRequest().body(
                Map.of("Error", "Email is already registered")
        );
    }
}
