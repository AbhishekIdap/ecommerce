package com.app.ecommerce.controller;

import com.app.ecommerce.service.ProductService;
import com.app.ecommerce.service.OrderService;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        int totalProducts = productService.getAllProducts().size();
        int totalOrders = orderService.getAllOrders().size();
        int totalUsers = userService.getAllUsers().size();
        int pendingOrders = orderService.getPendingOrders().size();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pendingOrders", pendingOrders);

        return "admin-dashboard";
    }
}
