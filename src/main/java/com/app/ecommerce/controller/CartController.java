package com.app.ecommerce.controller;

import com.app.ecommerce.model.Cart;
import com.app.ecommerce.model.Order;
import com.app.ecommerce.model.Product;
import com.app.ecommerce.model.User;
import com.app.ecommerce.service.OrderService;
import com.app.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // Get or create Cart from session
    private Cart getCartFromSession(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    /* =====================================================
       AJAX APIs for Add / Remove / Status / Count
       ===================================================== */

    // ✅ Add product via AJAX (used by product list & details)
    @PostMapping("/api/add/{id}")
    @ResponseBody
    public String addToCartAjax(@PathVariable int id, HttpSession session) {
        Product p = productService.getProductById(id);
        if (p != null) {
            Cart cart = getCartFromSession(session);
            cart.addItem(p);
        }
        return "OK";
    }

    // ✅ Remove product via AJAX
    @PostMapping("/api/remove/{id}")
    @ResponseBody
    public String removeFromCartAjax(@PathVariable int id, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.removeItem(id);
        return "OK";
    }

    // ✅ Status for one product: IN_CART / NOT_IN_CART
    @GetMapping("/api/status/{id}")
    @ResponseBody
    public String status(@PathVariable int id, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            return "NOT_IN_CART";
        }

        return cart.getItems().stream()
                .anyMatch(item -> item.getProduct().getId() == id)
                ? "IN_CART"
                : "NOT_IN_CART";
    }

    // ✅ Total count for navbar badge
    @GetMapping("/api/count")
    @ResponseBody
    public int cartCount(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            return 0;
        }
        return cart.getTotalItems();
    }

    /* =====================================================
       Normal (non-AJAX) mappings: cart page, buy now, etc
       ===================================================== */

    // Old GET add → redirect to cart (for safety / old links)
    @GetMapping("/add/{id}")
    public String addToCartGET(@PathVariable int id, HttpSession session) {
        Product p = productService.getProductById(id);
        if (p != null) {
            Cart cart = getCartFromSession(session);
            cart.addItem(p);
        }
        return "redirect:/cart";
    }

    // POST add (used by "Buy Now" form on product detail)
    @PostMapping("/add/{id}")
    public String addToCartPOST(@PathVariable int id, HttpSession session) {
        Product p = productService.getProductById(id);
        if (p != null) {
            Cart cart = getCartFromSession(session);
            cart.addItem(p);
        }
        return "redirect:/cart";
    }

    @GetMapping
    public String showCart(HttpSession session, Model model) {
        Cart cart = getCartFromSession(session);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/remove/{id}")
    public String removeItem(@PathVariable int id, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.removeItem(id);
        return "redirect:/cart";
    }

    @GetMapping("/increase/{id}")
    public String increase(@PathVariable int id, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.increaseQuantity(id);
        return "redirect:/cart";
    }

    @GetMapping("/decrease/{id}")
    public String decrease(@PathVariable int id, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.decreaseQuantity(id);
        return "redirect:/cart";
    }

    @PostMapping("/place-order")
    public String placeOrder(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = getCartFromSession(session);

        if (cart.getItems().isEmpty()) {
            model.addAttribute("message", "Your cart is empty!");
            return "order-status";
        }

        String userId = user.getUserId(); // username

        Order order = orderService.placeOrder(userId, cart);

        // Add required data for Thymeleaf
        model.addAttribute("order", order);
        model.addAttribute("userName", user.getUserId());
        model.addAttribute("totalPrice", cart.getTotalPrice());

        // Reset the cart
        session.setAttribute("cart", new Cart());

        return "order-success";
    }


    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getOrdersForUser(username));
        return "orders";
    }

    @GetMapping("/admin/orders")
    public String allOrdersForAdmin(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getAllOrders());
        return "orders-admin";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable int id,
                               HttpSession session,
                               Model model) {

        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/cart/orders";
        }

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (!order.getUserId().equals(username) &&
                (role == null || !role.equals("ADMIN"))) {
            return "redirect:/products";
        }

        model.addAttribute("order", order);
        return "order-details";
    }
}
