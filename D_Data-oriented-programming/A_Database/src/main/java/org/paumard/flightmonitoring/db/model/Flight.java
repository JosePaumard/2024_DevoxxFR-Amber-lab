package org.paumard.flightmonitoring.db.model;

public class Flight {
    private IDFlight id;
    private City from;
    private City to;
    private Price price;
    private Plane plane;

    public Flight(IDFlight id, City from, City to, Price price, Plane plane) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.price = price;
        this.plane = plane;
    }

    public IDFlight id() {
        return this.id;
    }

    public City from() {
        return this.from;
    }

    public City to() {
        return this.to;
    }

    public Price price() {
        return this.price;
    }

    public void updatePrice(Price price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Flight[id=" + id + ", from=" + from + ", city=" + to +
               ", price=" + price + ", plane = " + plane + "]";
    }
}
