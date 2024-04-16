package org.paumard.flightmonitoring.pricemonitoring.model;

public class FlightID {
    private String flightId;

    public FlightID(String id) {
        this.flightId = id;
    }

    public String flightId() {
        return this.flightId;
    }

    @Override
    public String toString() {
        return "FlightID[flightId=" + flightId + "]";
    }
}
