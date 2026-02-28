package com.app.ecommerce.service;

import com.app.ecommerce.model.Product;
import com.app.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repo;

    @Override
    public List<Product> getAllProducts() {
        return repo.findAll()
                .stream()
                .filter(p -> !p.isDeleted())   // hide deleted products
                .collect(Collectors.toList());
    }

    @Override
    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void saveProduct(Product product) {
        product.setDeleted(false);
        repo.save(product);
    }

    // 🔥 Soft Delete (SET deleted = true)
    @Override
    public void deleteProduct(int id) {
        Product p = repo.findById(id).orElse(null);
        if (p != null) {
            p.setDeleted(true);
            repo.save(p);
        }
    }

    // 🔥 UNDO delete (SET deleted = false)
    @Override
    public void undoDelete(int id) {
        Product p = repo.findById(id).orElse(null);
        if (p != null) {
            p.setDeleted(false);
            repo.save(p);
        }
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return repo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream()
                .filter(p -> !p.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getSortedProducts(String sortType) {
        List<Product> list = getAllProducts(); // only non-deleted products

        switch (sortType) {
            case "priceLowHigh":
                return list.stream()
                        .sorted(Comparator.comparingDouble(Product::getPrice))
                        .collect(Collectors.toList());

            case "priceHighLow":
                return list.stream()
                        .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                        .collect(Collectors.toList());

            case "nameAZ":
                return list.stream()
                        .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList());

            case "nameZA":
                return list.stream()
                        .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed())
                        .collect(Collectors.toList());

            default:
                return list;
        }
    }
}
