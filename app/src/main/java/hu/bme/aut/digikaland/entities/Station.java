package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

/**
 * Created by Sylent on 2018. 02. 28..
 */

public class Station implements Serializable {
    public int id;
    public int number;
    public Status status;
    public enum Status{
        NotStarted,
        Started,
        Done
    }

    public Station(int i, int n, Status s){
        id = i;
        number = n;
        status = s;
    }
}
