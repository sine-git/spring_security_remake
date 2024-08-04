package com.security.springsecurityremake.controller;

import com.security.springsecurityremake.entity.AppUser;
import com.security.springsecurityremake.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/")
public class Pages {
    @Autowired
    AppUserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder ;
    @GetMapping("/users/home")
    String homePage()
    {
        return "Welcome to the Home Page";
    }

    @PostMapping(value ="/users/add")
    AppUser addUser(@RequestBody AppUser appUser)
    {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    @GetMapping("/admin/home")
    String adminPage()
    {
        return "Welcome to the Admin Page";
    }
}
