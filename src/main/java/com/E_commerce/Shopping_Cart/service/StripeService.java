package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.SecretKey;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @GetMapping("/success")
    public String loadSuccess() {
        return "/user/successPage";
    }

    @GetMapping("/paymentFailed")
    public String failedSuccess() {
        return "/user/failedPage";
    }

    public StripeResponse checkoutTotalAmount(Double totalAmount) {
        Stripe.apiKey = stripeSecretKey;  // Use the secret key from properties

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Order Payment")
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("inr")  // Use usd if your cart uses USD
                        .setUnitAmount((long) (totalAmount * 100))  // Convert to paise/cents
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setPriceData(priceData)
                        .setQuantity(1L)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080/user/success")
                        .setCancelUrl("http://localhost:8080/user/paymentFailed")
                        .addLineItem(lineItem)
                        .build();


        try {
            Session session = Session.create(params);
            return StripeResponse.builder()
                    .status("Success")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .message("Payment session created")
                    .build();
        } catch (StripeException e) {
            e.printStackTrace();
            return StripeResponse.builder()
                    .status("Failed")
                    .message("Failed to create Stripe session: " + e.getMessage())
                    .build();
        }
    }
}
