package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.UserDtl;

import java.util.List;

public interface UserService {

    public UserDtl saveUser(UserDtl user);

    public UserDtl saveAdmin(UserDtl user);

    public UserDtl getUserByEmail(String email);

    public List<UserDtl> getAllUsers(String role);

    Boolean updateAccountStatus(Integer id, boolean status);

    // Lecture 18
    public void increaseFailedAttempt(UserDtl userDtl);

    public void userAccountLock(UserDtl userDtl);

    public boolean unlockAccountTimeExpired(UserDtl userDtl);

    public void resetAttempt(int userId);

    public void updateUserResetToken(String email,String resetToken);

    public UserDtl getUserByToken(String token);

    public UserDtl updateUser(UserDtl user);

    public Boolean existsEmail(String email);
}