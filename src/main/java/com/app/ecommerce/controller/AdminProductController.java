package com.app.ecommerce.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.app.ecommerce.model.Product;
import com.app.ecommerce.service.ProductService;
import com.app.ecommerce.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductService service;

    @Autowired
    private OrderService orderService;

    private Product lastDeletedProduct;  // backup for undo

    @GetMapping("/dashboard")
    public String adminHome(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role) ? "admin-dashboard" : "redirect:/login";
    }

    @GetMapping("/products")
    public String productList(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) return "redirect:/login";

        model.addAttribute("products", service.getAllProducts());
        return "admin-product-list";
    }

    @GetMapping("/add")
    public String addProductPage(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/save")
    public String saveProduct(HttpSession session, Product product) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        service.saveProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable int id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        model.addAttribute("product", service.getProductById(id));
        return "edit-product";
    }

    @PostMapping("/update")
    public String updateProduct(HttpSession session, Product product) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        service.saveProduct(product);
        return "redirect:/admin/products";
    }

    // DELETE WITH UNDO SUPPORT
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        lastDeletedProduct = service.getProductById(id);  // backup

        if (lastDeletedProduct != null) {
            service.deleteProduct(id);  // REAL DELETE
            redirectAttributes.addFlashAttribute("msg", "Product deleted successfully!");
            redirectAttributes.addFlashAttribute("undoId", id);
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/undo/{id}")
    public String undoDelete(@PathVariable int id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";

        if (lastDeletedProduct != null && lastDeletedProduct.getId() == id) {
            service.saveProduct(lastDeletedProduct); // RESTORE
            lastDeletedProduct = null;
            redirectAttributes.addFlashAttribute("msg", "Undo successful! Product restored.");
        }

        return "redirect:/admin/products";
    }
}
