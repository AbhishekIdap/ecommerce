package com.app.ecommerce.repository;

import com.app.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(String userId);
    List<Order> findByStatus(String status);
}
