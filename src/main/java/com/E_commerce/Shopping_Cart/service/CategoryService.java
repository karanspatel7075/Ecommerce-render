package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.Category;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface CategoryService {

    public Category saveCategory(Category category);            // Save Category

    public Boolean existCategory(String name);                  // Existence of Category

    public List<Category> getAllCategory();                     // List of Category

    public Boolean deleteCategory(int id);                      // Delete the Category

    public Category getCategoryById(int id);                    // Get Category by id

    public List<Category> getAllActiveCategory();

    public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize);

}
