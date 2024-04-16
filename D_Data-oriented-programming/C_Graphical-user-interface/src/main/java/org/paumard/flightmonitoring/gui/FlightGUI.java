package org.paumard.flightmonitoring.gui;

import org.paumard.flightmonitoring.business.model.City;
import org.paumard.flightmonitoring.business.model.MultilegFlight;
import org.paumard.flightmonitoring.business.model.SimpleFlight;
import org.paumard.flightmonitoring.business.service.FlightGUIService;
import org.paumard.flightmonitoring.business.model.Flight;

public class FlightGUI implements FlightGUIService {

    public void displayFlight(Flight flight) {
        switch(flight) {
            case SimpleFlight(_, City from, City to) -> System.out.println(
                    "Flight from " + from.name() + " to " + to.name() +
                    ": price is now " + SimpleFlight.price((SimpleFlight)flight));
            case MultilegFlight(_, City from, City via, City to) -> System.out.println(
                    "Flight from " + from.name() + " to " + to.name() + " via " + via.name() +
                    ": price is now " + MultilegFlight.price((MultilegFlight)flight));
        };
    }
}
