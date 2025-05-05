package com.E_commerce.Shopping_Cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private UserDtl user;

    @ManyToOne(fetch = FetchType.EAGER)
    private AddProduct product;

    @NotNull
    private Integer quantity;

    @Transient
    private Double totalPrice;

//    @Transient
    private Double totalOrderPrice;

}
