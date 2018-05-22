package hu.bme.aut.digikaland.entities.station;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.enumeration.StationStatusFromClient;

/**
 * A kliens szemszögéből egy állomás.
 */
public class StationClientPerspective implements Serializable {
    public Station station;
    public StationStatusFromClient status;

    public StationClientPerspective(String i, int n, StationStatusFromClient s){
        station = new Station(i,n);
        status = s;
    }
}
