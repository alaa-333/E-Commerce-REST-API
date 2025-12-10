package com.e_commerce.E_Commerce.REST.API.model.enums;

public enum OrderStatus {

    PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED;

    public boolean canModifyItems() {
        return this == PENDING || this == CONFIRMED;
    }
}
