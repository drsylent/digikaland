package hu.bme.aut.digikaland.entities.station;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

import hu.bme.aut.digikaland.entities.objectives.Objective;

/**
 * Egy állomást reprezentáló osztály.
 */
public class Station implements Serializable, Comparable<Station> {
    // egy sorrend - később kivehető, de akkor StationClientPerspective-be át kell helyezni!
    public int number;

    @Override
    public int compareTo(@NonNull Station station) {
        return this.id.compareTo(station.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Station && ((Station) obj).id.equals(id);
    }

    public String id;

    public Station(String i, int n){
        id = i;
        number = n;
    }
}
