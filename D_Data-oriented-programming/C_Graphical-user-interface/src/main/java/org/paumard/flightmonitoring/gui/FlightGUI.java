package org.paumard.flightmonitoring.gui;

import org.paumard.flightmonitoring.business.service.FlightGUIService;
import org.paumard.flightmonitoring.business.model.Flight;

public class FlightGUI implements FlightGUIService {

    public void displayFlight(Flight flight) {
        System.out.println(
                "Flight from " + flight.from().name() + " to " + flight.to().name() +
                ": price is now " + flight.price());
    }
}
