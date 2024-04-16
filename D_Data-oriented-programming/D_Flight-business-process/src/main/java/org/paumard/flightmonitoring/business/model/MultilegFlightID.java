package org.paumard.flightmonitoring.business.model;

public record MultilegFlightID(String from, String via, String to) implements FlightID {
}
