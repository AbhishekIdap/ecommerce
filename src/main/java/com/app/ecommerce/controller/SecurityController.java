package com.app.ecommerce.controller;

import com.app.ecommerce.model.User;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SecurityController {

    @Autowired
    private UserService userService;

    // ---------- helper ----------
    private User getLoggedUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return null;
        return userService.getByUsername(username);
    }

    // ============ MAIN SECURITY PAGE ============
    @GetMapping("/security")
    public String securityHome(HttpSession session, Model model,
                               @RequestParam(value = "msg", required = false) String msg,
                               @RequestParam(value = "err", required = false) String err) {

        User user = getLoggedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);

        // fake 2FA flag in session just for UI demo
        Boolean twoFA = (Boolean) session.getAttribute("twoFA");
        if (twoFA == null) twoFA = false;
        model.addAttribute("twoFAEnabled", twoFA);

        return "security-login";
    }

    // ============ CHANGE PASSWORD ============
    @PostMapping("/security/change-password")
    public String changePassword(HttpSession session,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword) {

        User user = getLoggedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        if (!user.getPassword().equals(currentPassword)) {
            return "redirect:/security?err=Current+password+is+incorrect";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/security?err=New+password+and+confirm+password+do+not+match";
        }

        if (newPassword.length() < 6) {
            return "redirect:/security?err=Password+must+be+at+least+6+characters";
        }

        user.setPassword(newPassword);
        userService.save(user);

        return "redirect:/security?msg=Password+updated+successfully";
    }

    // ============ UPDATE CONTACT DETAILS ============
    @PostMapping("/security/update-contact")
    public String updateContact(HttpSession session,
                                @RequestParam String email,
                                @RequestParam String phone) {

        User user = getLoggedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        user.setEmail(email);
        user.setPhone(phone);
        userService.save(user);

        return "redirect:/security?msg=Contact+details+updated";
    }

    // ============ TOGGLE 2FA (just stored in session for now) ============
    @PostMapping("/security/toggle-2fa")
    public String toggleTwoFA(HttpSession session,
                              @RequestParam(required = false) String enabled) {

        Boolean twoFA = "on".equals(enabled);
        session.setAttribute("twoFA", twoFA);

        String msg = twoFA ? "Two-factor authentication enabled"
                           : "Two-factor authentication disabled";

        return "redirect:/security?msg=" + msg.replace(" ", "+");
    }

    // ============ LOGOUT ALL SESSIONS (here: current one) ============
    @PostMapping("/security/logout-all")
    public String logoutAll(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ============ DELETE ACCOUNT (simple demo) ============
    @PostMapping("/security/delete-account")
    public String deleteAccount(HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        // simple soft delete for demo: change role to DELETED
        user.setRole("DELETED");
        userService.save(user);

        session.invalidate();
        return "redirect:/login";
    }

    // ============ LOGIN ACTIVITY PAGE (demo data for now) ============
    @GetMapping("/security/logins")
    public String loginActivity(HttpSession session, Model model) {

        User user = getLoggedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // demo records (no DB yet)
        List<LoginRecord> records = new ArrayList<>();
        records.add(new LoginRecord(LocalDateTime.now().minusHours(1),
                "Mumbai, India", "Chrome · Windows 11", true));
        records.add(new LoginRecord(LocalDateTime.now().minusDays(1),
                "Mumbai, India", "Android App", true));
        records.add(new LoginRecord(LocalDateTime.now().minusDays(3),
                "Pune, India", "Edge · Laptop", false));

        model.addAttribute("records", records);

        return "login-activity";
    }

    // small inner class just to display activity data
    public static class LoginRecord {
        private LocalDateTime time;
        private String location;
        private String device;
        private boolean success;

        public LoginRecord(LocalDateTime time, String location, String device, boolean success) {
            this.time = time;
            this.location = location;
            this.device = device;
            this.success = success;
        }

        public LocalDateTime getTime() { return time; }
        public String getLocation() { return location; }
        public String getDevice() { return device; }
        public boolean isSuccess() { return success; }
    }
}
