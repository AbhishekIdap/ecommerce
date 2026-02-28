package com.app.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.app.ecommerce.model.Product;
import com.app.ecommerce.service.ProductService;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // ===================== PRODUCT LIST + SORTING ======================
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "sort", required = false) String sort,
                               Model model,
                               HttpSession session) {

        String role = (String) session.getAttribute("role");
        boolean isAdmin = role != null && role.equals("ADMIN");

        model.addAttribute("isAdmin", isAdmin);

        List<Product> products;

        if (sort == null) {
            products = productService.getAllProducts();
        } else {
            products = productService.getSortedProducts(sort);
        }

        model.addAttribute("products", products);
        model.addAttribute("sort", sort);
        model.addAttribute("products", productService.getAllProducts());

        return "index"; 
    }

    // 🔥 LIVE SEARCH API → returns JSON
    @GetMapping("/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam("query") String query) {
        return productService.searchProducts(query);
    }

    // ⭐ PRODUCT DETAILS PAGE
    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable("id") int id, Model model) {

        Product product = productService.getProductById(id);

        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        return "product-details";
    }

    // ⭐⭐⭐ ADD PRODUCT WITH IMAGE UPLOAD (MATCHES add-product.html) ⭐⭐⭐
    @PostMapping("/admin/products/add")
    public String saveProduct(@RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam("price") double price,
                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        String fileName = imageFile.getOriginalFilename();

        // Save to static/product_images folder
        Path uploadPath = Paths.get("src/main/resources/static/product_images");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = imageFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(fileName);

        productService.saveProduct(product);

        return "redirect:/admin/products";
    }

    // ⭐⭐⭐ UPDATE PRODUCT WITH IMAGE URL (FROM edit-product.html) ⭐⭐⭐
    @PostMapping("/admin/products/update")
    public String updateProduct(@RequestParam("id") int id,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam("imageUrl") String imageUrl) {

        Product product = productService.getProductById(id);

        if (product != null) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);
            productService.saveProduct(product);
        }

        return "redirect:/admin/products";
    }
}
