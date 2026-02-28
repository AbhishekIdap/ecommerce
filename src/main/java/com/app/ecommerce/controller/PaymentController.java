package com.app.ecommerce.controller;

import com.app.ecommerce.model.PaymentMethod;
import com.app.ecommerce.model.User;
import com.app.ecommerce.service.PaymentService;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    // Helper: find logged-in user
    private User getLoggedUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return null;
        return userService.getByUsername(username);
    }

    // ========== MAIN OPTIONS PAGE ==========
    @GetMapping("/options")
    public String paymentOptions(HttpSession session, Model model) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        List<PaymentMethod> methods = paymentService.getMethodsByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("methods", methods);

        return "payment-options";
    }

    // ========== SAVED METHODS LIST PAGE ==========
    @GetMapping("/saved")
    public String savedMethods(HttpSession session, Model model) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        List<PaymentMethod> methods = paymentService.getMethodsByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("methods", methods);

        return "saved-payment-methods";
    }

    // ========== ADD UPI (FORM PAGE) ==========
    @GetMapping("/add/upi")
    public String showAddUpi(HttpSession session, Model model) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "payment-upi-add";
    }

    // ========== ADD UPI (FORM SUBMIT) ==========
    @PostMapping("/add/upi")
    public String addUpi(HttpSession session,
                         @RequestParam String upiId,
                         @RequestParam String provider,
                         @RequestParam String label) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        PaymentMethod m = new PaymentMethod();
        m.setUserId(user.getId());
        m.setType("UPI");
        m.setUpiId(upiId);
        m.setProvider(provider);
        m.setLabel(label);

        paymentService.save(m);

        return "redirect:/payment/options";
    }

    // ========== ADD CARD (FORM PAGE) ==========
    @GetMapping("/add/card")
    public String showAddCard(HttpSession session, Model model) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "payment-card-add";
    }

    // ========== ADD CARD (FORM SUBMIT) ==========
    @PostMapping("/add/card")
    public String addCard(HttpSession session,
                          @RequestParam String cardNumber,
                          @RequestParam String cardHolder,
                          @RequestParam String cardBrand,
                          @RequestParam String expiryMonth,
                          @RequestParam String expiryYear,
                          @RequestParam String label) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        String last4 = cardNumber.length() >= 4
                ? cardNumber.substring(cardNumber.length() - 4)
                : cardNumber;

        PaymentMethod m = new PaymentMethod();
        m.setUserId(user.getId());
        m.setType("CARD");
        m.setCardLast4(last4);
        m.setCardHolder(cardHolder);
        m.setCardBrand(cardBrand);
        m.setExpiryMonth(expiryMonth);
        m.setExpiryYear(expiryYear);
        m.setLabel(label);
        m.setProvider(cardBrand);

        paymentService.save(m);

        return "redirect:/payment/options";
    }

    // ========== SET DEFAULT ==========
    @PostMapping("/set-default/{id}")
    public String setDefault(HttpSession session,
                             @PathVariable int id) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        paymentService.setDefault(user.getId(), id);
        return "redirect:/payment/options";
    }

    // ========== DELETE METHOD ==========
    @PostMapping("/delete/{id}")
    public String deleteMethod(HttpSession session,
                               @PathVariable int id) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        PaymentMethod pm = paymentService.getById(id);
        if (pm != null && pm.getUserId() == user.getId()) {
            paymentService.delete(id);
        }

        return "redirect:/payment/options";
    }
}
