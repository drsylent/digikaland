package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

public class Team implements Serializable{
    public String name;
    public EvaluationStatus status;

    public Team(String n, EvaluationStatus s){
        name = n;
        status = s;
    }
}
