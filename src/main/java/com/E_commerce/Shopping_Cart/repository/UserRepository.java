package com.E_commerce.Shopping_Cart.repository;

import com.E_commerce.Shopping_Cart.model.UserDtl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDtl, Integer> {

    public UserDtl findByEmail(String email);

    public UserDtl getUserByEmail(String email);

    List<UserDtl> findByRole(String role);

    public UserDtl findByResetToken(String token);

    public Boolean existsByEmail(String email);

}
