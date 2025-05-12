package com.E_commerce.Shopping_Cart.Controller;

import com.E_commerce.Shopping_Cart.model.AddProduct;
import com.E_commerce.Shopping_Cart.model.Category;
import com.E_commerce.Shopping_Cart.model.UserDtl;
import com.E_commerce.Shopping_Cart.service.CartService;
import com.E_commerce.Shopping_Cart.service.CategoryService;
import com.E_commerce.Shopping_Cart.service.ProductService;
import com.E_commerce.Shopping_Cart.service.UserService;
import com.E_commerce.Shopping_Cart.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

//import static com.E_commerce.Shopping_Cart.util.CommonUtil.sendMail;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if(p != null) {
            String email = p.getName();
            UserDtl user = userService.getUserByEmail(email);
            m.addAttribute("user", user);

            // user hona chaiye toh hi wrna null Exception
            Integer countCart = cartService.getCountCart(user.getId());
            m.addAttribute("countCart", countCart);
        }
    }

    @GetMapping("/")
    public String homePage(Model model, Principal principal) {
        List<Category> allActiveCategory = categoryService.getAllCategory().stream().limit(6).toList();
//        List<AddProduct> allProducts = productService.getAllActiveProductIgnoreCase("").stream().limit(8).toList();

        List<AddProduct> allProducts = productService.getAllActiveProductIgnoreCase("");
        System.out.println("Total active products: " + allProducts.size());
        List<AddProduct> limited = allProducts.stream().limit(12).toList();
        model.addAttribute("category", allActiveCategory);
        model.addAttribute("product", allProducts);
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String indexPage(Model model, Principal principal) {
        List<Category> allActiveCategory = categoryService.getAllCategory().stream().limit(12).toList();
//        List<AddProduct> allProducts =

        List<AddProduct> allProducts = productService.getAllActiveProductIgnoreCase("");
        System.out.println("Total active products: " + allProducts.size());
        List<AddProduct> limited = allProducts.stream().limit(20).toList();

        model.addAttribute("listActive", categoryService.getAllActiveCategory());

        model.addAttribute("category", allActiveCategory);
        model.addAttribute("product", limited); // ✅ Now passes only 12 products to the view
        return "index";
    }

    @GetMapping("/signin")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "registration";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam (value = "category", defaultValue = "") String category, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize, @RequestParam(name = "ch", required = false, defaultValue = "") String ch) {
        System.out.println("category : " + category);
        List<Category> categoryList = categoryService.getAllActiveCategory();
//        List<AddProduct> addProductList = productService.getAllActiveProductIgnoreCase(category);
//        m.addAttribute("product", addProductList);
        m.addAttribute("categories", categoryList);
        m.addAttribute("paramValue", category);

        m.addAttribute("listActive", categoryService.getAllActiveCategory());

        Page<AddProduct> page;
        if(StringUtils.isEmpty(ch)) {
            page = productService.getAllActivePagination(pageNo, pageSize, category);
        }
        else {
            page = productService.searchProductPagination(pageNo, pageSize, ch);
        }

        // Pagination
        m.addAttribute("product", page.getContent());   // actual product list shown on current page
        m.addAttribute("pageNo", page.getNumber());     // current page number
        m.addAttribute("pageSize", pageSize);           // items per page
        m.addAttribute("totalElement", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "products";
    }

    @GetMapping("/product/{id}")
    public String viewProducts(@PathVariable("id") int id, Model m, HttpSession session) {
        AddProduct productById = productService.getProductById(id);
        m.addAttribute("product", productById);

        // Get 4 random recommended products (excluding current product)
        List<AddProduct> recommended = productService.getRandomProducts(4, id);
        m.addAttribute("recommendedProducts", recommended);

//        // Optionally add user info
//        UserDtl user = (UserDtl) session.getAttribute("user");
//        m.addAttribute("user", user);
//        System.out.println("Recommended products size: " + recommended.size());
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String userSave(@ModelAttribute UserDtl userDtl, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {

        Boolean existingEmail = userService.existsEmail(userDtl.getEmail());
        if(existingEmail) {
            session.setAttribute("Success", "Email already Exists");
        }
         else {
            String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
            userDtl.setProfileImage(imageName);

            UserDtl saveUser = userService.saveUser(userDtl);

            if(saveUser != null) {
//                try {
//                    File savefile = new ClassPathResource("static/img/profile_img").getFile();
//
//                    Path paths = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
//
//                    Files.copy(file.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);
//
//                    System.out.println("Image saved at : " + paths);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                    String uploadDir = System.getProperty("user.dir") + "/images/profile_image";
                    File savefile = new File(uploadDir);

                    // Ensure the directory exists
                    if (!savefile.exists()) {
                        savefile.mkdirs();
                    }

                Path paths = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), paths, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("Success", "Profile saved successfully");
            }
            else {
                session.setAttribute("errorMsg", "Something went wrong on server site");
            }
        }
         return "redirect:/register";
    }

    // Forget Password logic
    @GetMapping("/forgetPassword")
    public String forgetPassword() {
        return "forget_password";
    }

    @PostMapping("/forgetPassword")
    public String processForgetPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDtl userByEmail = userService.getUserByEmail(email);

        if(ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Invalid email");
        }
        else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            // Generate URL http://localhost:8080/resetPassword?token=asdsfrgafxcvafg

            String url = CommonUtil.generatorUrl(request)+"/resetPassword?token="+resetToken;

            Boolean sendMail = commonUtil.sendMail(url, email);

            if(sendMail) {
                session.setAttribute("Success", "Please check your email.. Password Reset Link is sent");
            }
            else {
                session.setAttribute("errorMsg", "Something went Wrong");
            }
        }
        return "redirect:/forgetPassword";  // redirect with new data
    }

    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam String token, HttpSession session, Model model) {
        UserDtl userByToken = userService.getUserByToken(token);

        if(userByToken == null) {
            model.addAttribute("errorMsg", "Your link is invalid or expired");
            return "error";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam String token,@RequestParam String password, HttpSession session, Model model) {
        UserDtl userByToken = userService.getUserByToken(token);

        if(userByToken == null) {
            model.addAttribute("errorMsg", "Your link is invalid or expired");
            return "error";
        }
        else {
           userByToken.setPassword(bCryptPasswordEncoder.encode(password));
           userByToken.setResetToken(null);
           userService.updateUser(userByToken);
           model.addAttribute("errorMsg", "Password was changed successfully");
           return "redirect:/signin";
        }
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m) {
        List<AddProduct> search = productService.searchProduct(ch);
        m.addAttribute("product", search);

        List<Category> categoryList = categoryService.getAllActiveCategory();
        m.addAttribute("categories", categoryList);
        return "products";
    }

}
