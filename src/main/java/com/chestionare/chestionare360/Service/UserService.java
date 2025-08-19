package com.chestionare.chestionare360.Service;

import com.chestionare.chestionare360.Model.Dto.UserCreateDto;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.UserRepo;
import com.chestionare.chestionare360.Service.SecurityService.MyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

//    public UserDetails createUser(String email, String rawPassword) {
//        if (userRepo.existsByEmail(email)) {
//            throw new RuntimeException("User already exists");
//        }
//
//        User newUser = new User();
//        newUser.setEmail(email);
//        newUser.setPassword(passwordEncoder.encode(rawPassword));
//        newUser.setRole("ROLE_USER");
//
//
//        userRepo.save(newUser);
//        return new MyUser(newUser);
//    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizatorul cu acest email nu a fost găsit"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
