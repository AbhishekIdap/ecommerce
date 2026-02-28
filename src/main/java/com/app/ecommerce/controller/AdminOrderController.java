package com.app.ecommerce.controller;

import com.app.ecommerce.model.Order;
import com.app.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // ===================== LIST ALL ORDERS =====================
    @GetMapping("/orders")
    public String listOrders(HttpSession session, Model model) {

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        return "admin-orders";   // admin-orders.html
    }

    // ===================== ORDER DETAILS =====================
    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable int id,
                               HttpSession session,
                               Model model) {

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/admin/orders";
        }

        model.addAttribute("order", order);
        return "admin-order-details";   // admin-order-details.html
    }
}
