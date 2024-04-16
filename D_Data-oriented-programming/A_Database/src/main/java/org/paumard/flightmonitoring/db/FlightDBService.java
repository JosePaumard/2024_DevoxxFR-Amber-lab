package org.paumard.flightmonitoring.db;

import org.paumard.flightmonitoring.business.model.City;
import org.paumard.flightmonitoring.business.model.Flight;
import org.paumard.flightmonitoring.business.model.FlightID;
import org.paumard.flightmonitoring.business.service.DBService;
import org.paumard.flightmonitoring.db.model.IDFlight;

import java.util.HashMap;
import java.util.Map;

public class FlightDBService implements DBService {

    private static Map<String, City> cities = Map.ofEntries(
            Map.entry("Pa", new City("Paris")),
            Map.entry("Lo", new City("London")),
            Map.entry("Am", new City("Amsterdam")),
            Map.entry("Fr", new City("Francfort")),
            Map.entry("NY", new City("New York")),
            Map.entry("Wa", new City("Washington")),
            Map.entry("At", new City("Atlanta")),
            Map.entry("Mi", new City("Miami"))
    );

    private static Map<IDFlight, Flight> flights = new HashMap<>();

    public Flight fetchFlight(FlightID id) {
        System.out.println("Fetching flight " + id);

        IDFlight flightId = new IDFlight(id.id());
        return flights.computeIfAbsent(flightId,
                _ -> {
                    var from = flightId.flightId().substring(0, 2);
                    var to = flightId.flightId().substring(2);
                    return new Flight(cities.get(from), cities.get(to));
                });
    }
}
