package com.app.ecommerce.service;

import com.app.ecommerce.model.Product;
import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    Product getProductById(int id);

    void saveProduct(Product product);

    void deleteProduct(int id);        // soft delete (setDeleted = true)

    void undoDelete(int id);           // undo soft delete (setDeleted = false)

    List<Product> searchProducts(String keyword);

    List<Product> getSortedProducts(String sortType);
}
