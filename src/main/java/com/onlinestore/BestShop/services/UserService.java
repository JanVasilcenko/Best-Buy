package com.onlinestore.BestShop.services;

import com.onlinestore.BestShop.exceptions.DuplicateUserException;
import com.onlinestore.BestShop.model.RegisterUserRequest;
import com.onlinestore.BestShop.model.User;
import com.onlinestore.BestShop.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByID(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void registerUser(RegisterUserRequest registerUserRequest){
         if (userRepository.existsByEmailIgnoreCase(registerUserRequest.getEmail())){
             throw new DuplicateUserException();
         }

         User newlyRegisteredUser = new User();
         newlyRegisteredUser.setEmail(registerUserRequest.getEmail());
         newlyRegisteredUser.setPasswordHash(passwordEncoder.encode(registerUserRequest.getPassword()));

         userRepository.save(newlyRegisteredUser);
    }
}
