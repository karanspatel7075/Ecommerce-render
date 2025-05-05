package com.E_commerce.Shopping_Cart.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data   // Lombok annotation to generate boilerplate code (getters, setters, toString, equals, hashCode).
public class OrderAddress {

    // Stores user's shipping info / Used in ProductOrder

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;

    private String LastName;

    private String email;

    private String mobileNo;

    private String address;

    private String city;

    private String state;

    private String pincode;

}
