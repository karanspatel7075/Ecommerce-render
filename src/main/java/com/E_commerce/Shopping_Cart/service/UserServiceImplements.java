package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.UserDtl;
import com.E_commerce.Shopping_Cart.repository.UserRepository;
import com.E_commerce.Shopping_Cart.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplements implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Since passwordEncoder() in SecurityConfig returns BCryptPasswordEncoder, Spring will inject the correct bean.

    @Override
    public UserDtl saveUser(UserDtl user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);

        //lecture 18
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        String encodePassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodePassword);
        return userRepository.save(user);
    }

    @Override
    public UserDtl saveAdmin(UserDtl user) {
        user.setRole("ROLE_ADMIN");
        user.setIsEnable(true);

        //lecture 18
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        String encodePassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodePassword);
        return userRepository.save(user);
    }

    @Override
    public UserDtl getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public List<UserDtl> getAllUsers(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, boolean status) {
        Optional<UserDtl> findByUser= userRepository.findById(id);

        if(findByUser.isPresent()) {
            UserDtl userDtl = findByUser.get();
            userDtl.setIsEnable(status);
            userRepository.save(userDtl);
            return true;
        }
        return null;
    }

//    Lecture 18
    @Override
    public void increaseFailedAttempt(UserDtl userDtl) {
        int attempt = userDtl.getFailedAttempt() + 1;
        userDtl.setFailedAttempt(attempt);
        userRepository.save(userDtl);
    }

    @Override
    public void userAccountLock(UserDtl userDtl) {
        userDtl.setAccountNonLocked(false);
        userDtl.setLockTime(new Date());
        userRepository.save(userDtl);
    }

    @Override
    public boolean unlockAccountTimeExpired(UserDtl userDtl) {
        long time = userDtl.getLockTime().getTime();
        long unlockTime = time+ AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if(unlockTime < currentTime) {
            userDtl.setAccountNonLocked(true);
            userDtl.setFailedAttempt(0);
            userDtl.setLockTime(null);
            userRepository.save(userDtl);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int userId) {
        // we will code when needed
    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtl findByEmail = userRepository.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        userRepository.save(findByEmail);
    }

    @Override
    public UserDtl getUserByToken(String token) {
        return (userRepository.findByResetToken(token));
    }

    @Override
    @Transactional
    public UserDtl updateUser(UserDtl user) {
        return userRepository.save(user);
    }

    @Override
    public Boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
