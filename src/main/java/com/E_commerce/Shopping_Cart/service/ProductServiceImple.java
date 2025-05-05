package com.E_commerce.Shopping_Cart.service;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import com.E_commerce.Shopping_Cart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImple implements ProductService {

    // Service layer of Add Product Category

    @Autowired
    private ProductRepository productRepository;

    @Override
    public AddProduct save(AddProduct product) {                // Save
        return productRepository.save(product);
    }

    @Override
    public List<AddProduct> getAllProducts() {                  // List
        return productRepository.findAll();
    }

    @Override
    public Optional<AddProduct> findById(int id) {              // Id
        return productRepository.findById(id);
    }

    @Override
    public boolean deleteById(int id) {                         // Delete
        AddProduct product = productRepository.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Boolean existAddProduct(String name) {               // Check
        return productRepository.existsByTitle(name);
    }

    @Override
    public AddProduct getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public AddProduct update(AddProduct addProduct, MultipartFile image) {

        AddProduct product1 = getProductById(addProduct.getId());

        product1.setTitle(addProduct.getTitle());
        product1.setDescription(addProduct.getDescription());
        product1.setCategory(addProduct.getCategory());
        product1.setStock(addProduct.getStock());
        product1.setPrice(addProduct.getPrice());
        product1.setIsActive(addProduct.getIsActive());
        product1.setDiscount(addProduct.getDiscount());

        // Calculate discounted price
        Double discount = addProduct.getPrice() * (addProduct.getDiscount() / 100.0);
        Double discountedPrice2 = addProduct.getPrice() - discount;
        product1.setDiscountedPrice(discountedPrice2);

        // Save the product first

//        System.out.println("Updated discounted price: " + updatedProduct.getDiscountedPrice());
        if (image != null && !image.isEmpty()) {
            String imageName = image.getOriginalFilename();
            product1.setImage(imageName);
            try {
                File savefile = new ClassPathResource("static/img/product_img").getFile();

                Path paths = Paths.get(savefile.getAbsolutePath()+File.separator+image.getOriginalFilename());

                Files.copy(image.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);

                System.out.println(paths);

            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        AddProduct updatedProduct = productRepository.save(product1);
        return updatedProduct;
        }



    @Override
    public List<AddProduct> getAllActiveProductIgnoreCase(String category) {

        if(ObjectUtils.isEmpty(category)) {
            return  productRepository.findByisActiveTrue();
        }
        else {
          return  productRepository.findByCategoryIgnoreCase(category);
        }
    }

    @Override
    public List<AddProduct> searchProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);

    }

    @Override
    public Page<AddProduct> getAllActivePagination(Integer pageNo, Integer pageSize, String category) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<AddProduct> productPage = null;

        if(ObjectUtils.isEmpty(category)) {
            productPage = productRepository.findByIsActiveTrue(pageable);
        }
        else {
            productPage = productRepository.findByCategoryAndIsActiveTrue(category, pageable);
        }

        productRepository.findByIsActiveTrue(pageable);
        return productPage;
    }

    @Override
    public Page<AddProduct> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
    }

    @Override
    public Page<AddProduct> getAllProducts(Integer pageNo, Integer pageSize, String ch) {
        Pageable pageable= PageRequest.of(pageNo, pageSize);
        return productRepository.findAll(pageable);
    }

    public List<AddProduct> getRandomProducts(int count, int excludeId) {
        List<AddProduct> allProducts = productRepository.findAll();

        // Convert excludeId to Integer so we can safely use .equals()
        Integer exclude = excludeId;

        List<AddProduct> filtered = allProducts.stream()
                .filter(p -> !exclude.equals(p.getId()))
                .collect(Collectors.toList());

        Collections.shuffle(filtered);
        return filtered.stream().limit(count).collect(Collectors.toList());
    }


}
