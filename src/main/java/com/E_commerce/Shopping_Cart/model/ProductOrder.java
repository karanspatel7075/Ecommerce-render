package com.E_commerce.Shopping_Cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Stores each order's data / links to UserDtl, AddProduct, and OrderAddress
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    private LocalDate orderDate;

    @ManyToOne
    private AddProduct product;

    private Double price;

    private Integer quantity;

    @ManyToOne
    private UserDtl user;

    private String status;

    private String paymentType;

    @OneToOne(cascade = CascadeType.ALL) // Ek product ka ek address
    private OrderAddress orderAddress;

}
