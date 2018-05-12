package hu.bme.aut.digikaland.entities;

import android.support.annotation.NonNull;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

public class Team implements Serializable, Comparable<Team>{
    public String name;
    public EvaluationStatus status;
    public int arrivingNumber;
    public String id;

    @Override
    public int compareTo(@NonNull Team team) {
        return this.arrivingNumber-team.arrivingNumber;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Team && ((Team) obj).arrivingNumber == this.arrivingNumber;
    }

    public Team(String n, EvaluationStatus s){
        name = n;
        status = s;
    }
}
