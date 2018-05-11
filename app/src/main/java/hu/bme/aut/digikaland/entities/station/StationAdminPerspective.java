package hu.bme.aut.digikaland.entities.station;

import android.support.annotation.NonNull;

import java.io.Serializable;

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
