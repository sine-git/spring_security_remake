package com.security.springsecurityremake.auth;

import com.security.springsecurityremake.entity.AppUser;
import com.security.springsecurityremake.entity.Role;
import com.security.springsecurityremake.repository.AppUserRepository;
import com.security.springsecurityremake.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    JwtService jwtService;
    @Autowired
    AppUserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = AppUser.builder().username(registerRequest.getUsername()).
                password(passwordEncoder.encode(registerRequest.password)).roles(Role.USER).build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return  AuthenticationResponse.builder().token(jwtToken).build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        System.out.println("Inside authenticate the user credentials are "+ authenticationRequest.getUsername() + " " + authenticationRequest.getPassword());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        var appUser = userRepository.findByUsername(authenticationRequest.username);
        if (appUser.isEmpty()){
            throw new UsernameNotFoundException("");
        }
        var jwtToken = jwtService.generateToken(appUser.get());
        return  AuthenticationResponse.builder().token(jwtToken).build();

    }
}
