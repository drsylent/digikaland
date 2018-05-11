package hu.bme.aut.digikaland.entities.station;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

import hu.bme.aut.digikaland.entities.objectives.Objective;

public class Station implements Serializable, Comparable<Station> {
    public int oldId;
    // TODO: ez nem csak a kliens perspektívába kell?
    public int number;
    private ArrayList<Objective> objectives = null;

    public void addObjective(Objective obj){
        if(objectives == null) objectives = new ArrayList<>();
        objectives.add(obj);
    }

    @Override
    public int compareTo(@NonNull Station station) {
        return this.id.compareTo(station.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Station && ((Station) obj).id.equals(id);
    }

    public void setObjectives(ArrayList<Objective> set){
        objectives = set;
    }

    @Nullable
    public ArrayList<Objective> getObjectives(){
        return objectives;
    }

    public Station(int i, int n, @NonNull ArrayList<Objective> obj){
        oldId = i;
        number = n;
        objectives = obj;
    }

    public Station(int i, int n){
        oldId = i;
        number = n;
    }

    public String id;

    public Station(String i, int n){
        id = i;
        number = n;
    }
}
