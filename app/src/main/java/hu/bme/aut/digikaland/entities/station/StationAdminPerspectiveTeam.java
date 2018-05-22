package hu.bme.aut.digikaland.entities.station;

import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

/**
 * Egy állomás nézete úgy, hogy látni, egy adott csapatnak értékelése hogy áll ott.
 */
public class StationAdminPerspectiveTeam extends StationAdminPerspective {
    public EvaluationStatus status;

    public StationAdminPerspectiveTeam(Station station1, EvaluationStatus status1){
        station = station1;
        status = status1;
    }
}
