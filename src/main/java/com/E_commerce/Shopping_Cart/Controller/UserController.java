package com.E_commerce.Shopping_Cart.Controller;

import com.E_commerce.Shopping_Cart.model.*;
import com.E_commerce.Shopping_Cart.repository.OrderRepository;
import com.E_commerce.Shopping_Cart.repository.UserRepository;
import com.E_commerce.Shopping_Cart.service.*;
import com.E_commerce.Shopping_Cart.util.CommonUtil;
import com.E_commerce.Shopping_Cart.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.PanelUI;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderServiceImple orderServiceImple;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private StripeService stripeService;

    @GetMapping("")
    public String home(Model model, Principal principal) {
        List<Category> allActiveCategory = categoryService.getAllCategory().stream().limit(12).toList();
//        List<AddProduct> allProducts = productService.getAllActiveProductIgnoreCase("").stream().limit(8).toList();


        List<AddProduct> allProducts = productService.getAllActiveProductIgnoreCase("");
        System.out.println("Total active products: " + allProducts.size());
        List<AddProduct> limited = allProducts.stream().limit(12).toList();

        model.addAttribute("category", allActiveCategory);
        model.addAttribute("product", allProducts);
        return "index";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            UserDtl user = userService.getUserByEmail(email);
            m.addAttribute("user", user);

            // user hona chaiye toh hi wrna null Exception
            Integer countCart = cartService.getCountCart(user.getId());
            m.addAttribute("countCart", countCart);

            List<Category> getAllActiveCategory = categoryService.getAllActiveCategory();
            m.addAttribute("listActive", getAllActiveCategory);
        }
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);

        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Product add to cart failed !!");
        } else {
            session.setAttribute("Success", "Product added to cart successfully");
        }
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {
        UserDtl user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());

//      Add this to debug
        for (Cart c : carts) {
            if (c == null) {
                System.out.println("Null cart found!");
            } else {
                System.out.println("Cart ID: " + c.getId() + ", Qty: " + c.getQuantity() + ", Product: " + c.getProduct());
            }
        }
        m.addAttribute("carts", carts);

//      Calculate the overall total price using a stream
        Double totalOrderPrice = carts.stream()
                .mapToDouble(cart -> cart.getTotalPrice() != null ? cart.getTotalPrice() : 0.0)
                .sum();
        m.addAttribute("totalOrderPrice", totalOrderPrice);
        return "user/cart";
    }

    private UserDtl getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        UserDtl userDtl = userService.getUserByEmail(email);
        return userDtl;
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";
    }

    @GetMapping("/orderPage")
    public String orderPage(Principal p, Model m) {
        UserDtl user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());

        // Add this to debug
        for (Cart c : carts) {
            if (c == null) {
                System.out.println("Null cart found!");
            } else {
                System.out.println("Cart ID: " + c.getId() + ", Qty: " + c.getQuantity() + ", Product: " + c.getProduct());
            }
        }
        m.addAttribute("carts", carts);

//      Calculate the overall total price using a stream
        Double orderPrice = carts.stream()
                .mapToDouble(cart -> cart.getTotalPrice() != null ? cart.getTotalPrice() : 0.0)
                .sum();

//      Including tax / delivery charges
        Double totalOrderPrice = carts.stream()
                .mapToDouble(cart -> cart.getTotalPrice() != null ? cart.getTotalPrice() : 0.0)
                .sum() + 250 + 100;
        m.addAttribute("orderPrice", orderPrice);
        m.addAttribute("totalOrderPrice", totalOrderPrice);
        return "user/orderPage";
    }

//    @PostMapping("/saveOrder")
//    public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) {
//        // System.out.println(request);
//
//        // Get logged-in user details
//        UserDtl user = getLoggedInUserDetails(p);
//
//        // Save order and get the saved order back
//        ProductOrder savedOrder = orderService.saveOrder(user.getId(), request);
//
//        if (savedOrder != null) {
//        try {
//            // Send email after successful order placement
//            commonUtil.sendMailForProduct(savedOrder, "Successfully Ordered");
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            session.setAttribute("errorMsg", "Order placed, but email not sent!");
//            e.printStackTrace();
//        }
//    } else {
//        session.setAttribute("errorMsg", "Order failed! Please try again.");
//    }
//        // 3. Clear user's cart
//        cartService.clearCartByUserId(user.getId());
//        return "redirect:/user/success";
//    }

    @PostMapping("/saveOrder")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session, Model model, @RequestParam("paymentType") String paymentType, @RequestParam("totalOrderPrice") Double totalOrderPrice) {
        // System.out.println(request);

        // Get logged-in user details
        UserDtl user = getLoggedInUserDetails(p);

        // Save order and get the saved order back
        ProductOrder savedOrder = orderService.saveOrder(user.getId(), request);

        if (savedOrder != null) {
            try {
                // Send email after successful order placement
                commonUtil.sendMailForProduct(savedOrder, "Successfully Ordered");
            } catch (MessagingException | UnsupportedEncodingException e) {
                session.setAttribute("errorMsg", "Order placed, but email not sent!");
                e.printStackTrace();
            }
        } else {
            session.setAttribute("errorMsg", "Order failed! Please try again.");
        }
        // 🧠 Now check which payment type is selected
        if ("COD".equalsIgnoreCase(paymentType)) {
            // ➡ For Cash On Delivery, directly show success page
            cartService.clearCartByUserId(user.getId());
            return "user/successPage"; // Your COD success page
        } else if ("ONLINE".equalsIgnoreCase(paymentType)) {
            // ➡ For Online Payment, redirect to Stripe
            StripeResponse response = stripeService.checkoutTotalAmount(totalOrderPrice);

            if ("Success".equals(response.getStatus())) {
                cartService.clearCartByUserId(user.getId());
                return "redirect:" + response.getSessionUrl();
            } else {
                model.addAttribute("errorMsg", response.getMessage());
                return "user/failedPage";
            }
        } else {
            model.addAttribute("errorMsg", "Invalid payment type selected!");
            return "user/failedPage";
        }
    }

    @PostMapping("/createPaymentSession")
    public String createPaymentSession(@RequestParam("totalOrderPrice") Double totalOrderPrice, Model model) {
        // Call StripeService to create checkout session
        StripeResponse response = stripeService.checkoutTotalAmount(totalOrderPrice);

        if ("Success".equals(response.getStatus())) {
            // Redirect user to Stripe payment page
            return "redirect:" + response.getSessionUrl();
        } else {
            // In case of failure, show error message on page
            model.addAttribute("errorMsg", response.getMessage());
            return "/user/failedPage";
        }
    }


    @GetMapping("/success")
    public String loadSuccess() {
        return "user/successPage";
    }

    @GetMapping("/paymentFailed")
    public String failedSuccess() {
        return "user/failedPage";
    }

    @GetMapping("/userOrders")
    public String myOrders(Model m, Principal p) {
        UserDtl loginuser = getLoggedInUserDetails(p);
        List<ProductOrder> orders = orderService.getOrdersByUser(loginuser.getId());
        m.addAttribute("getOrders", orders);
        return "user/my_orders";
    }

    @GetMapping("/cancelOrder")
    public String cancelOrder(@RequestParam("orderId") Integer orderId, @RequestParam("st") String st, HttpSession session) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            session.setAttribute("Success", "Product had been cancelled successfully");
        } else {
            session.setAttribute("errorMsg", "Product cancel failed !!");
        }
        return "redirect:/user/userOrders";

    }

    // This is pending method for now
    @GetMapping("/updateStatus")
    public String userUpdatedStatus(@RequestParam("id") Integer id, @RequestParam("st") String st, HttpSession session) {
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
        return "redirect:/user/userOrders";
    }

    @GetMapping("/profile")
    public String profile(Principal p,  Model model) {
        if (p != null) {
            String email = p.getName();
            UserDtl user = userService.getUserByEmail(email);
            model.addAttribute("profile", user);
        }
        return "user/profile";
    }

    // Updating
    @GetMapping("/loadProfile")
    public String loadProfilePage(Principal p, Model model) {
        if (p != null) {
            String email = p.getName();
            UserDtl user = userService.getUserByEmail(email);
            model.addAttribute("profile", user);
        }
        return "user/editProfile";
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
                File savefile = new File("images/profile_image");

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
        return "redirect:/user/profile";
    }

}