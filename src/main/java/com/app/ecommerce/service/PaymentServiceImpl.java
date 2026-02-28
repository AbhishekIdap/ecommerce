package com.app.ecommerce.service;

import com.app.ecommerce.model.PaymentMethod;
import com.app.ecommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository repo;

    @Override
    public List<PaymentMethod> getMethodsByUserId(int userId) {
        return repo.findByUserId(userId);
    }

    @Override
    public PaymentMethod getById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public PaymentMethod save(PaymentMethod method) {
        if (method.getCreatedAt() == null) {
            method.setCreatedAt(LocalDateTime.now());
        }
        return repo.save(method);
    }

    @Override
    public void delete(int id) {
        repo.deleteById(id);
    }

    @Override
    public void setDefault(int userId, int paymentId) {
        // Remove default from all user methods
        List<PaymentMethod> list = repo.findByUserId(userId);
        for (PaymentMethod pm : list) {
            pm.setDefault(pm.getId() == paymentId);
        }
        repo.saveAll(list);
    }
}
