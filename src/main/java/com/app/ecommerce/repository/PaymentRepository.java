package com.app.ecommerce.repository;

import com.app.ecommerce.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentMethod, Integer> {

    List<PaymentMethod> findByUserId(int userId);

}
