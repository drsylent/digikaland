package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

/**
 * Created by Sylent on 2018. 04. 04..
 */

public class StationAdminPerspective implements Serializable {
    public Station station;
    public int evaluated;
    public int done;
    public int sum;

    public StationAdminPerspective(Station s, int e, int d, int su){
        station = s;
        evaluated = e;
        done = d;
        sum = su;
    }
}
