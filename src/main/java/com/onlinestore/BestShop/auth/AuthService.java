package com.onlinestore.BestShop.auth;

import com.onlinestore.BestShop.user.CustomUserDetails;
import com.onlinestore.BestShop.user.User;
import com.onlinestore.BestShop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        return userRepository.findByEmail(email).orElse(null);
    }

    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken){
        if(!jwtService.validateToken(refreshToken))
            throw new BadCredentialsException("Invalid refresh token");

        String email = jwtService.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);

        return accessToken;
    }
}
