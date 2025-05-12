package com.E_commerce.Shopping_Cart.util;

import com.E_commerce.Shopping_Cart.model.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    public Boolean sendMail(String url, String reciepentEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("ffg19162@gmail.com", "Shopping_Cart");
        helper.setTo(reciepentEmail);

        String content = "<p>Dear User,</p>"
                + "<p>Thank you for registering with <strong>Shopping Cart</strong>.</p>"
                + "<p>Please click the link below to verify your email address and activate your account:</p>"
                + "<p><a href=\"" + url + "\">Verify Email</a></p>"
                + "<br>"
                + "<p>If you did not request this, please ignore this email.</p>"
                + "<br>"
                + "<p>Regards,<br>Shopping Cart Team</p>";

        helper.setSubject("Password Reset");
        helper.setText(content, true);
        mailSender.send(message);

        return true;
    }

    public static String generatorUrl(HttpServletRequest request) {
        // Get full request URL, like http://localhost:8080/forgetPassword
        String siteUrl = request.getRequestURL().toString();

        // Remove the servlet path (/forgetPassword), leaving http://localhost:8080
        return siteUrl.replace(request.getServletPath(), "");
    }

    public Boolean sendMailForProduct(ProductOrder order, String status) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("ffg19162@gmail.com", "Shopping_Cart");
        helper.setTo(order.getOrderAddress().getEmail());

        String msg = "[name], Thank you for your ordering! .<br><br>" +
                "🧾 <b>Order Summary:</b><br>" +
                "• Product Category: [Category Name]<br>" +
                "• Product Name: [Product Name]<br>" +
                "• Quantity: [Quantity]<br>" +
                "• Payment Method: [Payment Type]<br><br>" +
                "We appreciate your purchase and will begin processing your order shortly.";

        OrderStatus[] values = OrderStatus.values();
        for (OrderStatus x : values) {
            if (x.getName().equalsIgnoreCase(status)) {
                msg = msg.replace("[orderStatus]", x.getName());
                break;
            }
        }

        msg = msg.replace("[name]", order.getOrderAddress().getFirstName());
        msg = msg.replace("[Category Name]", order.getProduct().getCategory());
        msg = msg.replace("[Product Name]", order.getProduct().getTitle());
        msg = msg.replace("[Quantity]", order.getQuantity().toString());
        msg = msg.replace("[Payment Type]", order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }
}
