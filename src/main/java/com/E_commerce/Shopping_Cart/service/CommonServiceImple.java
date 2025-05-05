package com.E_commerce.Shopping_Cart.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommonServiceImple implements CommonService {

    @Override
    public void removeSessionMessage() {
        HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpSession session = request.getSession();

        session.removeAttribute("Success");
        session.removeAttribute("errorMsg");
    }
}
