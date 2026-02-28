package com.app.ecommerce.service;

import com.app.ecommerce.model.Cart;
import com.app.ecommerce.model.Order;
import com.app.ecommerce.model.OrderItem;
import com.app.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository repo;

    @Override
    public Order placeOrder(String username, Cart cart) {

        Order order = new Order();
        order.setUserId(username);
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();

        cart.getItems().forEach(ci -> {
            OrderItem item = new OrderItem();

            // If your OrderItem has a "product" field:
            // item.setProduct(ci.getProduct());

            // If your OrderItem does NOT have product field but has name/price fields,
            // then uncomment/change these according to your model:
            // item.setName(ci.getProduct().getName());
            // item.setPrice(ci.getProduct().getPrice());

            item.setQuantity(ci.getQuantity());
            item.setOrder(order);

            items.add(item);
        });

        order.setItems(items);

        // If your Order entity has a total/amount field, set it here:
        // order.setTotalPrice(cart.getTotalPrice());
        // or order.setAmount(cart.getTotalPrice());
        // (leave it out if there is no such field)

        return repo.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return repo.findAll();
    }

    @Override
    public List<Order> getOrdersForUser(String username) {
        return repo.findByUserId(username);
    }

    @Override
    public List<Order> getPendingOrders() {
        return repo.findByStatus("PENDING");
    }

    @Override
    public Order getOrderById(int id) {
        return repo.findById(id).orElse(null);
    }
}
