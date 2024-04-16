package org.paumard.flightmonitoring.gui;

import org.paumard.flightmonitoring.db.model.Flight;

public class FlightGUI {

    public static FlightGUI getInstance() {
        return new FlightGUI();
    }

    public void displayFlight(Flight flight) {
        System.out.println(
                "Flight from " + flight.from().name() + " to " + flight.to().name() +
                ": price is now " + flight.price().price());
    }
}
