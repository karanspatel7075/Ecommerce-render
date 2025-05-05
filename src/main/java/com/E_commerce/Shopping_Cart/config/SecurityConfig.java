package com.E_commerce.Shopping_Cart.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  ////@Configuration tells Spring this class contains Bean definitions.
public class SecurityConfig {

    @Autowired
    private AuthSuccessHandlerImpl authSuccessHandler;

    @Autowired
    @Lazy
    private AuthFailureHandlerImpl authFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Passwords stored in DB should be hashed, not plain text.
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImple();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;  // It basically connects the pieces: user + password + role.
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {  // HttpSecurity is used to build and customize it.

        http.csrf(crsf -> crsf.disable()).cors(cors -> cors.disable())
                .authorizeHttpRequests(req ->req.requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(form-> form.loginPage("/signin")
                        .loginProcessingUrl("/login")
//                        .defaultSuccessUrl("/")
                        .failureHandler(authFailureHandler).successHandler(authSuccessHandler))
                .logout(logout-> logout.permitAll());


        return http.build();  // Builds the configured filter chain so Spring Security can use it
    }

}
