package hu.bme.aut.digikaland.entities.station;

import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

public class StationAdminPerspectiveTeam extends StationAdminPerspective {
    public EvaluationStatus status;

    public StationAdminPerspectiveTeam(Station station1, EvaluationStatus status1){
        station = station1;
        status = status1;
    }
}
