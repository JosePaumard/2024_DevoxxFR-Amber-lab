package org.paumard.flightmonitoring;

import org.paumard.flightmonitoring.business.FlightMonitoring;
import org.paumard.flightmonitoring.db.model.Flight;
import org.paumard.flightmonitoring.db.model.IDFlight;

public class Main {

    public static void main(String[] args) {

        var flightMonitoring = FlightMonitoring.getInstance();

        var f1 = new IDFlight("PaAt");
        var f2 = new IDFlight("AmNY");
        var f3 = new IDFlight("LoMi");
        var f4 = new IDFlight("FrWa");

        flightMonitoring.followFlight(f1);
        flightMonitoring.followFlight(f2);
        flightMonitoring.followFlight(f3);
        flightMonitoring.followFlight(f4);

        flightMonitoring.monitorFlight(f3);
        flightMonitoring.monitorFlight(f4);

        while (true) {

        }
    }
}