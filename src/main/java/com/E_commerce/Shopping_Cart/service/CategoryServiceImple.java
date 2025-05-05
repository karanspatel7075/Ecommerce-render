package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.Category;
import com.E_commerce.Shopping_Cart.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryServiceImple implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;                  // Injecting Repo

    public CategoryServiceImple(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category saveCategory(Category category) {                       // SAVE
        return categoryRepository.save(category);
    }

    @Override
    public Boolean existCategory(String name) {                             // CHECK
        return categoryRepository.existsByName(name);
    }

    @Override
    public List<Category> getAllCategory() {                                // FIND ALL
        return categoryRepository.findAll();
    }

    @Override
    public Boolean deleteCategory(int id) {                                 // DELETE
        Category cat = categoryRepository.findById(id).orElse(null);

        if(cat != null) {
            categoryRepository.delete(cat);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryById(int id) {                               // FIND BY ID
        Category cat = categoryRepository.findById(id).orElse(null);
        return cat;  // mene yaha pr null return kiya isiliye pura page ki mara gyi thi
    }

    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories = categoryRepository.findByisActiveTrue();
        return categories;
    }

    @Override
    public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return categoryRepository.findAll(pageable);
    }
}
