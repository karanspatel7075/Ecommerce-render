package com.E_commerce.Shopping_Cart.model;

import jakarta.persistence.*;
import lombok.*;

//@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categoryList")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String imageName;

    private Boolean isActive;
}
