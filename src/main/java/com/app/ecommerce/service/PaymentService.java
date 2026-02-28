package com.app.ecommerce.service;

import com.app.ecommerce.model.PaymentMethod;

import java.util.List;

public interface PaymentService {

    List<PaymentMethod> getMethodsByUserId(int userId);

    PaymentMethod getById(int id);

    PaymentMethod save(PaymentMethod method);

    void delete(int id);

    void setDefault(int userId, int paymentId);
}
