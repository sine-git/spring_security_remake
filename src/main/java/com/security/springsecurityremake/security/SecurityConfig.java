package com.security.springsecurityremake.security;

import com.security.springsecurityremake.config.JwtAuthenticationFilter;
import com.security.springsecurityremake.repository.AppUserRepository;
import jakarta.servlet.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    @Autowired
    AppUserDetailsService appUserDetailsService;

    @Autowired
    AppUserRepository appUserRepository ;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        return httpSecurity.csrf(AbstractHttpConfigurer::disable).sessionManagement(session->{
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }).authorizeHttpRequests(registry ->
        {
            registry.requestMatchers("/api/v1/auth/**").permitAll();
            registry.anyRequest().authenticated();

        }).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();

        /*httpSecurity.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(
                registry ->{
                    registry.requestMatchers("/login","/users/add").permitAll();
                  //registry.requestMatchers("/users/add").permitAll();
                    registry.requestMatchers("/users/home").hasRole("USERS");
                    registry.requestMatchers("/admin/home").hasRole("ADMINS");
                    registry.anyRequest().authenticated();
                }
        )

                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll).build();*/
    }

   /* @Bean
    UserDetailsService userDetailsService(){

    //   UserDetails userDetails=  User.builder().username("user").password("$2a$12$sipRmshm7ctAms1Jl1eDtu5pF6DLRnvmnCU2LMsW3BSh1ymg/JiQu").roles("USERS").build();

       return appUserDetailsService;

               //new InMemoryUserDetailsManager(userDetails);

    }*/

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return  provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

}
