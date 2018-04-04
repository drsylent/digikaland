package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

/**
 * Created by Sylent on 2018. 04. 04..
 */

public class Team implements Serializable{
    public String name;
    public EvaluationStatus status;

    public Team(String n, EvaluationStatus s){
        name = n;
        status = s;
    }
}
