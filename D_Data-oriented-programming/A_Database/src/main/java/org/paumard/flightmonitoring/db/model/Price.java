package org.paumard.flightmonitoring.db.model;

public class Price {
    private final int price;

    public Price(int price) {
        this.price = price;
    }

    public int price() {
        return this.price;
    }

    @Override
    public String toString() {
        return "Price[price=" + price + "]";
    }
}
