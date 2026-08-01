package com.garrettw011.orderflow.order;

public enum OrderStatus {
    PENDING("PENDING"),
    RESERVED("RESERVED"),
    PAID("PAID"),
    FULFILLING("FULFILLING"),
    SHIPPED("SHIPPED"),
    CANCELED("CANCELED"),
    FAILED("FAILED");

    private final String name;

    private OrderStatus(String name) { this.name = name; }

    public String toStr() { return this.name; }
}
