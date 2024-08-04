package com.security.springsecurityremake.security;

import com.security.springsecurityremake.entity.AppUser;
import com.security.springsecurityremake.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    AppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser appUser = appUserRepository.findByUsername(username).get();
        if (appUser != null) {
          //  return User.builder().username(appUser.getUsername()).password(appUser.getPassword()).roles(appUser.getRoles()).build() ;
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
