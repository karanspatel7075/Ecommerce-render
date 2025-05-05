package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {


    public AddProduct save(AddProduct product);                 // Save

    public List<AddProduct> getAllProducts();                   // Get All

    public Optional<AddProduct> findById(int id);               // Find By ID

    public boolean deleteById(int id);                          // Delete

    public Boolean existAddProduct(String name);                // Check

    public AddProduct getProductById(Integer id);

    public AddProduct update(AddProduct addProduct, MultipartFile image);

    public List<AddProduct> getAllActiveProductIgnoreCase(String category);

    public List<AddProduct> searchProduct(String ch); // For searching only

    public Page<AddProduct> getAllActivePagination(Integer pageNo, Integer pageSize, String category);

    public Page<AddProduct> searchProductPagination(Integer pageNo, Integer pageSize, String ch); // for searching + pagination

    public Page<AddProduct> getAllProducts(Integer pageNo, Integer pageSize, String ch);

    List<AddProduct> getRandomProducts(int i, int id);
}
