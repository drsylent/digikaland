package hu.bme.aut.digikaland;

import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Station;
import hu.bme.aut.digikaland.entities.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.enumeration.StationStatusFromClient;

/**
 * Created by Sylent on 2018. 04. 04..
 */

public class StationAdminPerspectiveTeam extends StationAdminPerspective {
    public EvaluationStatus status;

    public StationAdminPerspectiveTeam(Station station1, EvaluationStatus status1){
        station = station1;
        status = status1;
    }
}
