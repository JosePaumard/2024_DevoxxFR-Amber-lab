package org.paumard.flightmonitoring.business.util;

import org.paumard.flightmonitoring.business.model.*;

public class Flights {

    public static void updatePrice(FlightID flightID, Price price) {
        switch (flightID) {
            case SimpleFlightID id -> SimpleFlight.updatePrice(id, price);
            case MultilegFlightID id -> MultilegFlight.updatePrice(id, price);
        }
    }
}
