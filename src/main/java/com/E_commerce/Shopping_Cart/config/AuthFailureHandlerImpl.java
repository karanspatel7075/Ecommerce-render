package com.E_commerce.Shopping_Cart.config;

import com.E_commerce.Shopping_Cart.model.UserDtl;
import com.E_commerce.Shopping_Cart.repository.UserRepository;
import com.E_commerce.Shopping_Cart.service.UserService;
import com.E_commerce.Shopping_Cart.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        UserDtl userDtl = userRepository.getUserByEmail(email); // Used wrong business logic (findByEmail)

        if (userDtl == null) {
            exception = new LockedException("Invalid email or password");
        }
        else if (userDtl.getIsEnable()) {
            if(userDtl.getAccountNonLocked())  { // true
                 if(userDtl.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                     userService.increaseFailedAttempt(userDtl);
                     // 1
                     // 2
                     // 3 ( Lock )
                 }
                 else {
                     userService.userAccountLock(userDtl);
                     exception = new LockedException("Your Account is locked !! Failed");
                 }
            }
            else {

                if (userService.unlockAccountTimeExpired(userDtl)) {
                    exception = new LockedException("Your account is unlock !! Please try to login");
                } else {
                    exception = new LockedException("Your account is locked !! Please try again later");
                }
            }
        }
        else {
            exception = new LockedException("Your account is inactive");
        }

        request.getSession().setAttribute("error", exception.getMessage());

        super.setDefaultFailureUrl("/signin?error");   // "If login fails, redirect the user to this URL: /signin?error."
        super.onAuthenticationFailure(request, response, exception);   // This line executes the built-in logic for failure handling provided by Spring Security.
    }

}
