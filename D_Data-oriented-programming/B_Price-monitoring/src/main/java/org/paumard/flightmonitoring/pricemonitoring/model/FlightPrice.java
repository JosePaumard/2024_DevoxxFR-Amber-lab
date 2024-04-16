package org.paumard.flightmonitoring.pricemonitoring.model;

public class FlightPrice {
    private final int price;

    public FlightPrice(int price) {
        this.price = price;
    }

    public int price() {
        return this.price;
    }

    @Override
    public String toString() {
        return "FlightPrice[price=" + price + "]";
    }
}
