package hu.bme.aut.digikaland.entities.station;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.enumeration.StationStatusFromClient;

public class StationClientPerspective implements Serializable {
    public Station station;
    public StationStatusFromClient status;

    public StationClientPerspective(Station sstation, StationStatusFromClient sstatus){
        station = sstation;
        status = sstatus;
    }

    public StationClientPerspective(int i, int n, StationStatusFromClient s){
        station = new Station(i, n);
        status = s;
    }

    public StationClientPerspective(String i, int n, StationStatusFromClient s){
        station = new Station(i,n);
        status = s;
    }
}
