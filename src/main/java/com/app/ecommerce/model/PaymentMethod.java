package com.app.ecommerce.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // FK to User (like Address.userId)
    private int userId;

    // BASIC INFO
    private String type;        // "UPI", "CARD", "NET_BANKING", "WALLET"
    private String provider;    // "Google Pay", "PhonePe", "SBI", "HDFC", "Paytm" etc
    private String label;       // "My GPay UPI", "Axis Credit Card" (user-friendly name)

    // UPI SPECIFIC
    private String upiId;       // "name@oksbi"

    // CARD SPECIFIC
    private String cardLast4;   // "1234"
    private String cardBrand;   // "VISA", "MASTERCARD", etc
    private String cardHolder;
    private String expiryMonth; // "01".."12"
    private String expiryYear;  // "25".."30"

    // OTHER
    private boolean isDefault;
    private LocalDateTime createdAt;

    public PaymentMethod() {}

    // ===== GETTERS & SETTERS =====

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
