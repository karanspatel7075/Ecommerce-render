package com.E_commerce.Shopping_Cart.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Collection< ? extends GrantedAuthority> authorities = authentication.getAuthorities(); //Get all the roles/permissions granted to the user (like ROLE_USER, ROLE_ADMIN).

        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);
        //Convert those authorities into a Set<String> of role names for easy comparison.

        if(roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin");
        }
        else if (roles.contains("ROLE_USER")) {
            response.sendRedirect("/user"); // ✅ Update this line
        }
        else {
            response.sendRedirect("/");
        }
    }
}
