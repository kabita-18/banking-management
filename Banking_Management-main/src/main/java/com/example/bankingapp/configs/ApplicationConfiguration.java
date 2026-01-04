package com.example.bankingapp.configs;

import com.example.bankingapp.model.RegisterUser;

import com.example.bankingapp.repository.RegisterUsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationConfiguration implements UserDetailsService {

    @Autowired
    private RegisterUsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        RegisterUser user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
        
        System.out.println("Authentication3");
        System.out.println("Loaded roles from DB: " + user.getRoles());

        return user;
    }
}
