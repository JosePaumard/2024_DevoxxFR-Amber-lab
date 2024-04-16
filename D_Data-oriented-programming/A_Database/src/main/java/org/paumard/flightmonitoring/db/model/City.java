package org.paumard.flightmonitoring.db.model;

public class City {
    private final String name;

    public City(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return "City[name=" + name + "]";
    }
}
