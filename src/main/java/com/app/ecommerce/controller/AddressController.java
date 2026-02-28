package com.app.ecommerce.controller;

import com.app.ecommerce.model.Address;
import com.app.ecommerce.model.User;
import com.app.ecommerce.service.AddressService;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    // VIEW ALL ADDRESSES
    @GetMapping
    public String listAddresses(HttpSession session, Model model) {

        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        User user = userService.getByUsername(username);
        model.addAttribute("addresses",
                addressService.getAddressesByUserId(user.getId()));

        return "address-list";
    }

    // ADD NEW ADDRESS FORM
    @GetMapping("/add")
    public String addAddressForm(Model model) {
        model.addAttribute("address", new Address());
        return "address-form";
    }

    // SAVE NEW ADDRESS
    @PostMapping("/save")
    public String saveAddress(
            HttpSession session,
            @ModelAttribute Address address
    ) {
        String username = (String) session.getAttribute("username");
        User user = userService.getByUsername(username);

        address.setUserId(user.getId());
        addressService.save(address);

        return "redirect:/profile/addresses";
    }

    // EDIT ADDRESS FORM
    @GetMapping("/edit/{id}")
    public String editAddress(@PathVariable int id, Model model) {
        model.addAttribute("address", addressService.getById(id));
        return "address-form";
    }

    // DELETE ADDRESS
    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable int id) {
        addressService.delete(id);
        return "redirect:/profile/addresses";
    }
}
