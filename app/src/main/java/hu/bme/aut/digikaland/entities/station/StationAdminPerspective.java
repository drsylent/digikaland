package hu.bme.aut.digikaland.entities.station;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.EvaluationStatistics;

/**
 * Egy állomás egy admin szemszögéből.
 */
public abstract class StationAdminPerspective implements Serializable, Comparable<StationAdminPerspective> {
    public Station station;

    @Override
    public int compareTo(@NonNull StationAdminPerspective stationPerspective) {
        return this.station.id.compareTo(stationPerspective.station.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StationAdminPerspective && ((StationAdminPerspective) obj).station.id.equals(this.station.id);
    }
}
