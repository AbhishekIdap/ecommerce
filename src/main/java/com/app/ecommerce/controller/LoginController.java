package com.app.ecommerce.controller;

import com.app.ecommerce.model.User;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session) {

        User user = userService.getByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            return "login";
        }

        // Store session info
        session.setAttribute("user", user);
        session.setAttribute("username", user.getUserId());
        session.setAttribute("role", user.getRole());
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("user", user);  // Full User object
        session.setAttribute("username", user.getUserId());  // Optional


        // Redirect based on role
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin-dashboard";
        } else {
            return "redirect:/portal";   // 🔥 User lands on portal FIRST
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           HttpSession session) {

        User existing = userService.getByUsername(username);
        if (existing != null) {
            return "register"; // username already exists
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("USER");

        userService.save(user);

        // Auto login after register
        session.setAttribute("user", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("userId", user.getUserId());

        return "redirect:/portal";   // 🔥 go to portal after register
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
}
