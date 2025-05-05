package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import com.E_commerce.Shopping_Cart.model.Cart;
import com.E_commerce.Shopping_Cart.model.UserDtl;
import com.E_commerce.Shopping_Cart.repository.CartRepository;
import com.E_commerce.Shopping_Cart.repository.ProductRepository;
import com.E_commerce.Shopping_Cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CartServiceImple implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        Cart existingCart = cartRepository.findByProductIdAndUserId(productId, userId);
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + 1);
            return cartRepository.save(existingCart);
        } else {
            Cart cart = new Cart();
            cart.setProduct(productRepository.findById(productId).get());
            cart.setUser(userRepository.findById(userId).get());
            cart.setQuantity(1); // Make sure to set this!
            return cartRepository.save(cart);
        }
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        Double totalOrderPrice = 0.0;

        // Iterate through each cart and update its individual total
        for (Cart c : carts) {
            Double itemTotal = c.getProduct().getDiscountedPrice() * c.getQuantity();
            c.setTotalPrice(itemTotal);   // ← each cart has its total set here
            totalOrderPrice += itemTotal;
        }
        // Optionally: if you want to print or log the overall total
        System.out.println("Overall Total Order Price: " + totalOrderPrice);
        return carts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);
        return countByUserId;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        Integer updateQuantity;
        if(sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;

            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        }
            else {
                updateQuantity = cart.getQuantity()+1;
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        }

    public void clearCartByUserId(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(carts);
    }
}

