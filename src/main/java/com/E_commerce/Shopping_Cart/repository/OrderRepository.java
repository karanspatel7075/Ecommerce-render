package com.E_commerce.Shopping_Cart.repository;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import com.E_commerce.Shopping_Cart.model.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<ProductOrder, Integer> {

    List<ProductOrder> findByUserId(Integer userId);

    ProductOrder findByOrderId(String orderId);

    Page<ProductOrder> findByOrderId(String orderId, Pageable pageable); // // ✅ Now filters by ID

}
