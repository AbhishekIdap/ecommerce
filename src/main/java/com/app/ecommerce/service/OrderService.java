package com.app.ecommerce.service;

import com.app.ecommerce.model.Cart;
import com.app.ecommerce.model.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(String username, Cart cart);

    List<Order> getAllOrders();

    List<Order> getOrdersForUser(String username);

    List<Order> getPendingOrders();  // NEW

    Order getOrderById(int id);
}
