package hu.bme.aut.digikaland.entities.station;

import com.google.firebase.firestore.GeoPoint;

import hu.bme.aut.digikaland.entities.EvaluationStatistics;

/**
 * Created by Sylent on 2018. 04. 04..
 */

public class StationAdminPerspectiveSummary extends StationAdminPerspective {
    public GeoPoint location;
    public EvaluationStatistics statistics;

    public StationAdminPerspectiveSummary(Station s, EvaluationStatistics stats, GeoPoint l){
        station = s;
        statistics = stats;
        location = l;
    }
}
