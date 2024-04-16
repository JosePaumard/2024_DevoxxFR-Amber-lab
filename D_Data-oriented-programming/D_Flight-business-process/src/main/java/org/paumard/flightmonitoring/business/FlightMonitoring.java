package org.paumard.flightmonitoring.business;

import org.paumard.flightmonitoring.db.FlightDBService;
import org.paumard.flightmonitoring.db.model.Flight;
import org.paumard.flightmonitoring.db.model.IDFlight;
import org.paumard.flightmonitoring.db.model.Price;
import org.paumard.flightmonitoring.gui.FlightGUI;
import org.paumard.flightmonitoring.pricemonitoring.FlightPriceMonitoringService;
import org.paumard.flightmonitoring.pricemonitoring.model.FlightConsumer;
import org.paumard.flightmonitoring.pricemonitoring.model.FlightID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FlightMonitoring {

    private static final Map<IDFlight, Flight> monitoredFlights = new ConcurrentHashMap<>();

    private static final FlightDBService dbService =
            FlightDBService.getInstance();
    private static final FlightPriceMonitoringService priceMonitoringService =
            FlightPriceMonitoringService.getInstance();
    private static final FlightGUI flightGUIService =
            FlightGUI.getInstance();

    public static FlightMonitoring getInstance() {
        priceMonitoringService.updatePrices();
        launchDisplay();
        return new FlightMonitoring();
    }

    public void followFlight(IDFlight idFlight) {
        Flight flight = dbService.fetchFlight(idFlight);
        FlightID flightID = new FlightID(idFlight.flightId());
        FlightConsumer flightConsumer = price -> flight.updatePrice(new Price(price.price()));
        priceMonitoringService.followPrice(flightID, flightConsumer);
    }

    public void monitorFlight(IDFlight idFlight) {
        var flight = dbService.fetchFlight(idFlight);
        monitoredFlights.put(idFlight, flight);
    }

    public static void launchDisplay() {
        var executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
//            System.out.println("Displaying " + monitoredFlights.size() + " flights");
            for (var flight : monitoredFlights.values()) {
                flightGUIService.displayFlight(flight);
            }
        };
        executor.scheduleAtFixedRate(task, 0, 500, TimeUnit.MILLISECONDS);
    }
}