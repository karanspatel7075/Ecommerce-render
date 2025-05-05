package com.E_commerce.Shopping_Cart.config;

import com.E_commerce.Shopping_Cart.model.UserDtl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class CustomUser implements UserDetails {

    private UserDtl userDtl;

    public CustomUser(UserDtl userDtl) {
        this.userDtl = userDtl;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userDtl.getRole()); // Converts your user's role (ADMIN, USER, etc.) into a format Spring Security understands.
        return Arrays.asList(authority);                                                     // GrantedAuthority is what Spring uses to check permissions
    }


    @Override
    public String getPassword() {
        return userDtl.getPassword(); // Spring will compare this with the one user enters during login. Returns the user's password from the database.
    }

    @Override
    public String getUsername() {
        return userDtl.getEmail(); // Returns the email as the username (because you're using email for login).
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Always returns true = user's account is not expired.
    }

    @Override
    public boolean isAccountNonLocked() {
        return userDtl.getAccountNonLocked(); // Always true = account is not locked.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return  true; // Password (credentials) is not expired.
    }

    @Override
    public boolean isEnabled() {
        System.out.println("User Enabled Status: " + userDtl.getIsEnable()); // This checks whether the user is enabled or not (true or false from the DB)
        return userDtl.getIsEnable();        // Helpful if you want to block login for deactivated users.

    }

}


// Why Do We Do This?
//Spring Security doesn't know about your custom UserDetail model.
//So, we write this CustomUser class to tell Spring how to fetch the user info it needs to perform login, role checks, etc.