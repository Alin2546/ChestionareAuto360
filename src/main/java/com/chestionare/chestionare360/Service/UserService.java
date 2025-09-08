package com.chestionare.chestionare360.Service;

import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.UserRepo;
import com.chestionare.chestionare360.Service.SecurityService.MyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public void createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setRole("ROLE_USER");
        userRepo.save(user);
    }

    public UserDetails loginOrRegister(String phoneNumber, String rawPassword) {
        Optional<User> optionalUser = userRepo.findByPhoneNumber(phoneNumber);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                throw new IllegalArgumentException("Parolă greșită!");
            }
            return new MyUser(user);
        } else {
            User newUser = new User();
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            createUser(newUser);
            return new MyUser(newUser);
        }
    }

    public Optional<User> findByEmail(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber);
    }
}
