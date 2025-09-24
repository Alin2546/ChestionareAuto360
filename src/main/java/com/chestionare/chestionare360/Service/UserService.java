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

    public Optional<User> findByEmail(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber);
    }
}
