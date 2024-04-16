package org.paumard.flightmonitoring.db.model;

public class Plane {
    private String type;

    public Plane(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    @Override
    public String toString() {
        return "Plane[type=" + type + "]";
    }
}
