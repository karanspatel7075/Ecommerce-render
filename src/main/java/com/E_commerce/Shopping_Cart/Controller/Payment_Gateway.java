package com.E_commerce.Shopping_Cart.Controller;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import com.E_commerce.Shopping_Cart.model.Cart;
import com.E_commerce.Shopping_Cart.service.CategoryService;
import com.E_commerce.Shopping_Cart.service.StripeResponse;
import com.E_commerce.Shopping_Cart.service.StripeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


import java.io.IOException;

@Controller
@RequestMapping("/gateWay")
public class Payment_Gateway {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/placeOrder")
    public void placeOrder(@RequestParam("paymentType") String paymentType, HttpServletResponse response) throws IOException {
        Cart cart = new Cart();
        double totalAmount = cart.getTotalOrderPrice();  // Get the actual total price

        StripeResponse stripeResponse = stripeService.checkoutTotalAmount(totalAmount);  // Use the total price

        if ("Success".equals(stripeResponse.getStatus())) {
            response.sendRedirect(stripeResponse.getSessionUrl());
        } else {
            response.sendRedirect("/paymentFailed");
        }
    }

}
