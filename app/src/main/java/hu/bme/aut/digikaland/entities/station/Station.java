package hu.bme.aut.digikaland.entities.station;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

import hu.bme.aut.digikaland.entities.objectives.Objective;

public class Station implements Serializable {
    public int id;
    public int number;
    private ArrayList<Objective> objectives = null;

    public void addObjective(Objective obj){
        if(objectives == null) objectives = new ArrayList<>();
        objectives.add(obj);
    }

    public void setObjectives(ArrayList<Objective> set){
        objectives = set;
    }

    @Nullable
    public ArrayList<Objective> getObjectives(){
        return objectives;
    }

    public Station(int i, int n, @NonNull ArrayList<Objective> obj){
        id = i;
        number = n;
        objectives = obj;
    }

    public Station(int i, int n){
        id = i;
        number = n;
    }
}
