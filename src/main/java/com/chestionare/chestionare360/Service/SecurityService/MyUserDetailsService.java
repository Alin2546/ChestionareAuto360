package com.chestionare.chestionare360.Service.SecurityService;


import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByPhoneNumber(username).orElseThrow(() -> new UsernameNotFoundException("User not found with:" + username));
        if (user == null) {
            throw new UsernameNotFoundException("Invalid Phonenumber or password");
        }
        return new MyUser(user);
    }

}

