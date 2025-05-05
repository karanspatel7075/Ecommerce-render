package com.E_commerce.Shopping_Cart.config;

import com.E_commerce.Shopping_Cart.model.UserDtl;
import com.E_commerce.Shopping_Cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security uses UserDetailsService to load user details from the database during login

@Service
public class UserDetailsServiceImple implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDtl user = userRepository.findByEmail(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUser(user); // ✅ If the user exists, you return a CustomUser object that implements UserDetails.
        //This wraps your own UserDetail entity into something Spring Security can understand.
    }
}

// Flow Summary:

// User tries to log in with email and password.

// Spring calls loadUserByUsername(email)

// You fetch the user from the DB using the repository.

// If user not found, throw error.

// If user is found, wrap it in CustomUser and return.

// Spring uses this object to check password, role, status.