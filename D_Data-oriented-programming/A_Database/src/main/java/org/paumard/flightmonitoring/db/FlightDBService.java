package org.paumard.flightmonitoring.db;

import org.paumard.flightmonitoring.business.model.*;
import org.paumard.flightmonitoring.business.service.DBService;

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

    private static Map<FlightID, Flight> flights = new HashMap<>();

    public Flight fetchFlight(FlightID id) {
        System.out.println("Fetching flight " + id);

        return flights.computeIfAbsent(id,
                _ -> switch (id) {
                    case SimpleFlightID(String from, String to) ->
                            new SimpleFlight((SimpleFlightID)id, cities.get(from), cities.get(to));
                    case MultilegFlightID(String from, String via, String to) ->
                            new MultilegFlight((MultilegFlightID)id, cities.get(from), cities.get(via), cities.get(to));
                });
    }
}
