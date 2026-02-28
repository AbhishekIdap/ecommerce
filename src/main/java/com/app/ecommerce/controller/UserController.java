package com.app.ecommerce.controller;

import com.app.ecommerce.model.User;
import com.app.ecommerce.service.ProductService;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/portal")
    public String portal() {
        return "portal";
    }

    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("products", productService.getAllProducts());

        return "dashboard";
    }
    // ---------- VIEW PROFILE ----------
    @GetMapping("/profile")
    public String userProfile(HttpSession session, Model model) {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        User user = userService.getByUsername(username);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "profile";
    }

    // ---------- EDIT PROFILE (FORM PAGE) ----------
    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        User user = userService.getByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "edit-profile";
    }

    // ---------- HANDLE PROFILE UPDATE ----------
    @PostMapping("/profile/update")
    public String updateProfile(
            HttpSession session,

            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam(required = false) String altPhone,
            @RequestParam(required = false) String gender,

            @RequestParam(required = false) String addressLine1,
            @RequestParam(required = false) String addressLine2,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String country
    ) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        User user = userService.getByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        // Update details
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAltPhone(altPhone);
        user.setGender(gender);

        user.setAddressLine1(addressLine1);
        user.setAddressLine2(addressLine2);
        user.setCity(city);
        user.setState(state);
        user.setPincode(pincode);
        user.setCountry(country);

        userService.save(user);

        // back to profile
        return "redirect:/profile";
    }
}
