package org.paumard.flightmonitoring.business.service;

import org.paumard.flightmonitoring.business.model.FlightID;
import org.paumard.flightmonitoring.business.model.Price;

public interface FlightConsumer {
    void updateFlight(FlightID flightID, Price price);
}
