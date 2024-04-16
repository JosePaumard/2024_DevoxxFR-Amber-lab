package org.paumard.flightmonitoring.business.service;


import org.paumard.flightmonitoring.business.model.FlightID;

public interface PriceMonitoringService {
    void updatePrices();
    void followPrice(FlightID flightID, FlightConsumer consumer);
}
