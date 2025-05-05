package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.*;
import com.E_commerce.Shopping_Cart.repository.CartRepository;
import com.E_commerce.Shopping_Cart.repository.OrderRepository;
import com.E_commerce.Shopping_Cart.util.CommonUtil;
import com.E_commerce.Shopping_Cart.util.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImple implements OrderService {

    @Autowired
    private OrderRepository orderRepository;  // Used to save order data into the database.

    @Autowired
    private CartRepository cartRepository;  // Used to get all cart items of a specific user (findByUserId(userId)).

    @Autowired
    private CommonUtil commonUtil;

    @Override
    // This method is responsible for creating and saving an order for a user.
    public ProductOrder saveOrder(Integer userId, OrderRequest orderRequest) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for(Cart cart : carts) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountedPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName()); // mail from admin in future
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);
            ProductOrder saveOrder = orderRepository.save(order);

            try {
                commonUtil.sendMailForProduct(saveOrder, null);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public ProductOrder updateOrderStatus(Integer orderId, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(orderId);
        if(findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            ProductOrder updatedStatus = orderRepository.save(productOrder);
            return updatedStatus;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<ProductOrder> searchOrdersByOrderId(String orderId, Integer pageNo, Integer pageSize) {
        Pageable pageable= PageRequest.of(pageNo, pageSize);
        return orderRepository.findByOrderId(orderId, pageable);
    }
}
