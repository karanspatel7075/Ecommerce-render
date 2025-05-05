package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.OrderRequest;
import com.E_commerce.Shopping_Cart.model.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    public ProductOrder saveOrder(Integer userId, OrderRequest orderRequest);

    public List<ProductOrder> getOrdersByUser(Integer userId);

    ProductOrder updateOrderStatus(Integer orderId, String status);

    public List<ProductOrder> getAllOrders();

    public ProductOrder getOrdersByOrderId(String orderId);

    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);

    public Page<ProductOrder> searchOrdersByOrderId(String orderId, Integer pageNo, Integer pageSize);

}
