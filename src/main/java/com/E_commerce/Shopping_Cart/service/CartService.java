    package com.E_commerce.Shopping_Cart.service;

    import com.E_commerce.Shopping_Cart.model.Cart;
    import com.E_commerce.Shopping_Cart.model.UserDtl;

    import java.util.List;

    public interface CartService {

        public Cart saveCart(Integer productId, Integer userId);

        public List<Cart> getCartByUser(Integer userId);

        public Integer getCountCart(Integer userId);

        void updateQuantity(String sy, Integer cid);

        public void clearCartByUserId(Integer userId);

    }
