package hu.bme.aut.digikaland.entities.station;

/**
 * Created by Sylent on 2018. 04. 04..
 */

public class StationAdminPerspectiveSummary extends StationAdminPerspective {
    public int evaluated;
    public int done;
    public int sum;

    public StationAdminPerspectiveSummary(Station s, int e, int d, int su){
        station = s;
        evaluated = e;
        done = d;
        sum = su;
    }
}
