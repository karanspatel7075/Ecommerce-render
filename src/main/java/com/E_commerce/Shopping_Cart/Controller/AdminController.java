package com.E_commerce.Shopping_Cart.Controller;

import com.E_commerce.Shopping_Cart.model.*;
import com.E_commerce.Shopping_Cart.repository.UserRepository;
import com.E_commerce.Shopping_Cart.service.*;
import com.E_commerce.Shopping_Cart.util.CommonUtil;
import com.E_commerce.Shopping_Cart.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderServiceImple orderServiceImple;

    @Autowired
    private CommonUtil  commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // Interface for the admin
    @GetMapping("")
    public String index() {
        return "admin/index";
    }

    // List the Category inside the product creation
    @GetMapping("loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> listing = categoryService.getAllCategory();
        m.addAttribute("list", listing);
        return "admin/add_product";
    }

    // Save the Product
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute AddProduct product, HttpSession session, @RequestParam("file") MultipartFile image, @RequestParam("category") String categoryName) throws IOException {

        // If a file is provided, update the product's imageName; otherwise, set a default value
        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);

        // Set product's category field to the category name
        product.setCategory(categoryName);

        // Discount product
        product.setDiscount(0);
        product.setDiscountedPrice(product.getPrice());

        // Optionally set a default status if not provided
        if(product.getIsActive() == null) {
            product.setIsActive(true);
        }

        AddProduct savedProduct = productService.save(product);

        if (savedProduct != null) {
            try {
                File savefile = new ClassPathResource("static/img/product_img").getFile(); // it's been stored in target folder

                // Ensure the directory exists
                if (!savefile.exists()) {
                    savefile.mkdirs();
                }

                Path paths = Paths.get(savefile.getAbsolutePath()+File.separator+image.getOriginalFilename());

                Files.copy(image.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image saved at : " + paths);

            } catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("Success", "Successfully saved the product");
        } else {
            session.setAttribute("Failed", "Something went wrong in server");
        }
        return "redirect:/admin/loadAddProduct";
    }

    // List all Category in category creation
    @GetMapping("/category")
    public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
//        m.addAttribute("categories", categoryService.getAllCategory()); due to pagination

        List<Category> categoryList = categoryService.getAllActiveCategory();
        m.addAttribute("listActive", categoryList);
        Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);
        List<Category> category = page.getContent();
        m.addAttribute("categories", category);   // actual product list shown on current page
        m.addAttribute("pageNo", page.getNumber());     // current page number
        m.addAttribute("pageSize", pageSize);           // items per page
        m.addAttribute("totalElement", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "admin/category";
    }

    // Save Category
    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, HttpSession session, @RequestParam("file") MultipartFile file) throws IOException {

        // If a file is provided, update the category's imageName; otherwise, set a default value
        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        category.setImageName(imageName);

        // Optionally set a default status if not provided
        if(category.getIsActive() == null) {
            category.setIsActive(true); // or false, depending on your requirements
        }

        Category savedCategory = categoryService.saveCategory(category);

            if(savedCategory != null) {
                try {
                    File savefile = new ClassPathResource("static/img/category_img").getFile();

                    Path paths = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                    Files.copy(file.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("Image saved at : " + paths);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                session.setAttribute("Success", "Category saved successfully");
            }
            else {
                session.setAttribute("errorMsg", "Failed to save category");
            }
        return "redirect:/admin/category";
    }

    // Delete the Category
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        Boolean delete = categoryService.deleteCategory(id);

        if (delete) {
            session.setAttribute("success", "Category is deleted successfully");
        } else {
            session.setAttribute("failed", "Invalid Something went wrong");
        }
        return "redirect:/admin/category";
    }

    // Updating
    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));

        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        Category category1 = categoryService.getCategoryById(category.getId());

        if (category1 != null) {
            String imageName = file.isEmpty() ? category1.getImageName() : file.getOriginalFilename();
            category1.setImageName(imageName);

            category1.setName(category.getName());
            category1.setIsActive(category.getIsActive());
            category1.setImageName(imageName);  // did correction here ( it was store null value in db )

            // Save updated category
            Category update = categoryService.saveCategory(category1);

            if (update != null) {

                try {
                    File savefile = new ClassPathResource("static/img/category_img").getFile();

                    Path paths = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                    Files.copy(file.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("Image saved at : " + paths);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                session.setAttribute("Success", "Category saved successfully");
            } else {
                session.setAttribute("Failed", "Failed to update category");
            }
        } else {
            session.setAttribute("Failed", "Category not found");
        }
        return "redirect:/admin/loadEditCategory/" + category.getId();
    }
    

    @GetMapping("/getProducts")
    public String viewProducts(Model m, @RequestParam(name = "ch", required = false) String ch
            ,@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

//        List<AddProduct> listOfProduct = productService.getAllProducts();
//        m.addAttribute("listOfProduct", listOfProduct); // due to pagination hidden

        Page<AddProduct> page;

        // ✅ Conditional check for empty or null search keyword
        if (ch == null || ch.trim().isEmpty()) {
            page = productService.getAllProducts(pageNo, pageSize, ch); // get all if no search
        } else {
            page = productService.searchProductPagination(pageNo, pageSize, ch); // filtered results
        }

        m.addAttribute("listOfProduct", page.getContent());   // actual product list shown on current page
        m.addAttribute("pageNo", page.getNumber());     // current page number
        m.addAttribute("pageSize", pageSize);           // items per page
        m.addAttribute("totalElement", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        m.addAttribute("ch", ch);

        return "admin/view_Products";  // mistake you called /admin/view_product   instead of /admin/product's'
    }


    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        productService.deleteById(id);
        session.setAttribute("Success", "The product has been deleted successfully");
        return "redirect:/admin/getProducts";
    }

    // Just showing editing page
    @GetMapping("/editProduct/{id}")
    public String updateProduct(@PathVariable("id") int id, Model themodel) {
        themodel.addAttribute("product", productService.getProductById(id));
        themodel.addAttribute("category", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/update2")
    public String update2(@ModelAttribute AddProduct product, @RequestParam("file") MultipartFile image, HttpSession session, Model m) {

        if(product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("Failed", "Invalid discount");
        }
        else {
            AddProduct product2 = productService.update(product, image);
            if (!ObjectUtils.isEmpty(product2)) {
                session.setAttribute("Success", "Product updated successfully");
            } else {
                session.setAttribute("Failed", "Failed to update Product");
            }
        }
        return "redirect:/admin/getProducts";
    }

    // Most used block of code
    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if(p != null) {
            String email = p.getName();
            UserDtl user = userService.getUserByEmail(email);
            m.addAttribute("user", user);

            List<Category> list = categoryService.getAllActiveCategory();
            m.addAttribute("list", list);

            // user hona chaiye toh hi wrna null Exception
            Integer countCart = cartService.getCountCart(user.getId());
            m.addAttribute("countCart", countCart);
        }
    }

    @GetMapping("/users")
    public String getAllUsers(Model m, @RequestParam Integer type) {
        List<UserDtl> users;

        if(type==1) {
            users = userService.getAllUsers("ROLE_USER");
        }
        else {
            users = userService.getAllUsers("ROLE_ADMIN");
        }

        m.addAttribute("userType", type);
        m.addAttribute("users", users);
        return  "/admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session, @RequestParam Integer type) throws IOException{
        Boolean f = userService.updateAccountStatus(id, status);
        if(f) {
            session.setAttribute("success", "Account is updated");
        }
        else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/admin/users?type="+type;
    }

    @GetMapping("/getAllOrders")
    public String getAllOrders(Model m,@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
//      m.addAttribute("getOrders", orderServiceImple.getAllOrders());
        Page<ProductOrder> page = orderServiceImple.getAllOrdersPagination(pageNo, pageSize);

        m.addAttribute("getOrders", page.getContent());   // actual product list shown on current page
        m.addAttribute("pageNo", page.getNumber());     // current page number
        m.addAttribute("pageSize", pageSize);           // items per page
        m.addAttribute("totalElement", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
         return "/admin/orders";
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") Integer id, @RequestParam("st") String st, HttpSession session) {
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        ProductOrder updated = orderServiceImple.updateOrderStatus(id, st);
        try {
            commonUtil.sendMailForProduct(updated, st);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if (updated != null) {
            session.setAttribute("Success", "Product had been Updated successfully");
        } else {
            session.setAttribute("errorMsg", "Something went wrong !!");
        }
        return "redirect:/admin/getAllOrders";
    }

    @GetMapping("/search-order")
    public String searchProduct(@RequestParam(required = false) String orderId, Model m, HttpSession session,@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        ProductOrder order = orderServiceImple.getOrdersByOrderId(orderId);

//        if (ObjectUtils.isEmpty(order)) {
//            session.setAttribute("errorMsg", "Incorrect order Id");
//        } else {
            // Wrap single object in a list
//            List<ProductOrder> orders = new ArrayList<>();
//            orders.add(order);
//            m.addAttribute("getOrders", orders);  // Note: same model key as used in HTML

            Page<ProductOrder> page;

            if (orderId == null || orderId.trim().isEmpty()) {
                // If orderId is not provided, fetch all orders with pagination
                page = orderServiceImple.getAllOrdersPagination(pageNo, pageSize);
            } else {
                // If orderId is provided, search for orders by orderId and paginate the results
                page = orderServiceImple.searchOrdersByOrderId(orderId, pageNo, pageSize);
            }

            m.addAttribute("getOrders", page);   // actual product list shown on current page
            m.addAttribute("pageNo", page.getNumber());     // current page number
            m.addAttribute("pageSize", pageSize);           // items per page
            m.addAttribute("totalElement", page.getTotalElements());
            m.addAttribute("totalPages", page.getTotalPages());
            m.addAttribute("isFirst", page.isFirst());
            m.addAttribute("isLast", page.isLast());

        // If no orders found for the given orderId
        if (ObjectUtils.isEmpty(page.getContent())) {
            session.setAttribute("errorMsg", "No orders found with the provided order ID");
        }
        return "/admin/orders"; //  // Return the view with the orders
    }

    // Same from Home controller
    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m) {
        List<AddProduct> search = productService.searchProduct(ch);
        m.addAttribute("listOfProduct", search);
        return "/admin/view_Products";
    }

    @GetMapping("/add-admin")
    public String adminAdd() {
        return "/admin/add_admin";
    }

    @GetMapping("/adminProfile")
    public String view_AdminProfile(Principal p,  Model model) {
        if (p != null) {
            String email = p.getName();
            UserDtl user = userService.getUserByEmail(email);
            model.addAttribute("profile", user);
        }
        return "/admin/admin_profile";
    }

    // Updating
    @GetMapping("/loadProfile")
    public String loadProfilePage(Principal p, Model model) {
        if (p != null) {
            String email = p.getName();
            UserDtl user = userService.getUserByEmail(email);
            model.addAttribute("profile", user);
        }
        return "admin/edit_profile";
    }

    @PostMapping("/saveAdmin")
    public String userSave(@ModelAttribute UserDtl userDtl, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        userDtl.setProfileImage(imageName);

        UserDtl saveUser = userService.saveAdmin(userDtl);

        if(saveUser != null) {
            try {
                File savefile = new ClassPathResource("static/img/profile_img").getFile();
                Path paths = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("Image saved at: " + paths);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("Success", "Admin registered successfully");
        }
        else {
            session.setAttribute("errorMsg", "Something went wrong on server site");
        }
        return "redirect:/admin/add-admin";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute UserDtl user, Principal p, HttpSession session, @RequestParam("img") MultipartFile image, Model model) {
        String email = p.getName();
        UserDtl profileId = userService.getUserByEmail(email);

        profileId.setName(user.getName());
        profileId.setAddress(user.getAddress());
        profileId.setCity(user.getCity());
        profileId.setState(user.getState());
        profileId.setEmail(user.getEmail());
        profileId.setMobileNumber(user.getMobileNumber());
        profileId.setPincode(user.getPincode());

        String imageName = image.isEmpty() ? profileId.getProfileImage() : image.getOriginalFilename();
        profileId.setProfileImage(imageName);

        // Password change logic
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Hash and update the password
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            profileId.setPassword(hashedPassword);
        } else {
            // Keep the existing password if none is provided
            profileId.setPassword(profileId.getPassword());
        }

        UserDtl saveUpdate = userRepository.save(profileId);

        if (saveUpdate != null) {

            try {
                File savefile = new ClassPathResource("static/img/profile_img").getFile();

                Path paths = Paths.get(savefile.getAbsolutePath()+File.separator+image.getOriginalFilename());

                Files.copy(image.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image saved at : " + paths);

            } catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("Success", "profile saved successfully");
        } else {
            session.setAttribute("Failed", "Profile not found");
        }
        return "redirect:/admin/adminProfile";
    }
}