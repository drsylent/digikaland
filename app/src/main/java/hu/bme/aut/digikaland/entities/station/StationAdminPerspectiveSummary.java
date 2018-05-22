package hu.bme.aut.digikaland.entities.station;

import com.google.firebase.firestore.GeoPoint;

import hu.bme.aut.digikaland.entities.EvaluationStatistics;

/**
 * Egy állomás egy admin szemszögéből úgy, hogy látszanak az összesítő adatai.
 */
public class StationAdminPerspectiveSummary extends StationAdminPerspective {
    public double latitude;
    public double longitude;
    public EvaluationStatistics statistics;

    public StationAdminPerspectiveSummary(Station s, EvaluationStatistics stats, GeoPoint l){
        station = s;
        statistics = stats;
        latitude = l.getLatitude();
        longitude = l.getLongitude();
    }
}
