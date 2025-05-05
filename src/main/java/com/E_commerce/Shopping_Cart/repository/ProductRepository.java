package com.E_commerce.Shopping_Cart.repository;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<AddProduct, Integer> {
    public Boolean existsByTitle(String name);

    List<AddProduct> findByisActiveTrue();

    List<AddProduct> findByCategory(String category);

    List<AddProduct> findByCategoryIgnoreCase(String category);

    List<AddProduct> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);

    Page<AddProduct> findByIsActiveTrue(Pageable pageable);

    Page<AddProduct> findByCategoryAndIsActiveTrue(String category, Pageable pageable);

    Page<AddProduct> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2, Pageable pageable);

}
