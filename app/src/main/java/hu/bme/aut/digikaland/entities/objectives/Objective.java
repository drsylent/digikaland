package hu.bme.aut.digikaland.entities.objectives;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

/**
 * Ez az osztály reprezentál egy feladatot, melyet végre kell hajtani. Absztrakt osztály, a leszármazottai valósítják meg, hogy mit kell tenni.
 */
public abstract class Objective implements Serializable, Comparable<Objective>{
    // TODO: konstruktorba beépíthető majd, de még a mockkal kompatibilisre hagyjuk
    private String id;

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    private String stationId;

    public String getId(){ return id; }

    public void setId(String id){ this.id = id; }

    public Objective(){}

    // Befejezési idő az állomáshoz lesz kötve!
    private String question;

    @Override
    public int compareTo(@NonNull Objective objective) {
        return this.id.compareTo(objective.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Objective && ((Objective) obj).id.equals(id);
    }

    public Objective(String q){
        question = q;
    }

    public String getQuestion(){
        return question;
    }

    public abstract ObjectiveFragment createFragment();
}
