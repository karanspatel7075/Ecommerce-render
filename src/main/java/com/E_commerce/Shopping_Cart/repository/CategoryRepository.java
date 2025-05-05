package com.E_commerce.Shopping_Cart.repository;

import com.E_commerce.Shopping_Cart.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable; // ✅ CORRECT

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    public Boolean existsByName(String name);       // We made a mistake here

    public List<Category> findByisActiveTrue();

    Page<Category> findAll(Pageable pageable);
}
