package com.E_commerce.Shopping_Cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class AddProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 500)
    private String title;

    @Column(length = 5000)
    private String description;

    private String category;

    private double price;

    private int stock;

    private String image;

    private int discount;

    private double discountedPrice;

    private Boolean isActive;

    private String currency; // 🔥 <-- Add this!


    @Override
    public String toString() {
        return "AddProduct{" +
                "id=" + id +
                ", name='" + title + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                '}';
    }


}
