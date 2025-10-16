package com.onlinestore.BestShop.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterUserRequest registerUserRequest){
         if (userRepository.existsByEmailIgnoreCase(registerUserRequest.getEmail())){
             throw new DuplicateUserException();
         }

         User newlyRegisteredUser = new User();
         newlyRegisteredUser.setEmail(registerUserRequest.getEmail());
         newlyRegisteredUser.setPasswordHash(passwordEncoder.encode(registerUserRequest.getPassword()));
         newlyRegisteredUser.setRole(Role.USER);

         userRepository.save(newlyRegisteredUser);
    }
}
