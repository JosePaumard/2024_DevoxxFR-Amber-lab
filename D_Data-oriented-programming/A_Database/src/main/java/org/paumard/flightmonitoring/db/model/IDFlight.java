package org.paumard.flightmonitoring.db.model;

public class IDFlight {
    private String flightId;

    public IDFlight(String id) {
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
