package com.app.ecommerce.service;

import com.app.ecommerce.model.User;

import java.util.List;

public interface UserService {

    User getByUsername(String username);

    User save(User user);

    List<User> getAllUsers();   // NEW
    
}
