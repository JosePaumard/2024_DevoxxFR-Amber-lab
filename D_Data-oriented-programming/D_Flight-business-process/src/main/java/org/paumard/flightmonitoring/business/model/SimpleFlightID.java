package org.paumard.flightmonitoring.business.model;

public record SimpleFlightID(String from, String to) implements FlightID {
}
