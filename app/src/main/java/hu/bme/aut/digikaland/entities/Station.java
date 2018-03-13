package hu.bme.aut.digikaland.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.digikaland.entities.objectives.Objective;

/**
 * Created by Sylent on 2018. 02. 28..
 */

public class Station implements Serializable {
    public int id;
    public int number;
    public Status status;
    public ArrayList<Objective> objective;
    public enum Status{
        NotStarted,
        Started,
        Done
    }

    private boolean statusCheck(Status s){
        return s != Status.Started;
    }

    public Station(int i, int n, Status s, @NonNull ArrayList<Objective> obj){
        id = i;
        number = n;
        status = s;
        objective = obj;
    }

    public Station(int i, int n, Status s){
        if(!statusCheck(s))
            throw new RuntimeException("You must add a list of objectives to a station, if it is started!");
        id = i;
        number = n;
        status = s;
    }
}
