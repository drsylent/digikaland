package hu.bme.aut.digikaland.entities.station;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.EvaluationStatistics;

/**
 * Egy állomás térkép által használható nézete.
 */
public class StationMapData implements Serializable{
    public Station station;
    private double latitude;
    private double longitude;
    public LatLng getLocation(){
        return new LatLng(latitude, longitude);
    }
    private String specialName = null;
    public EvaluationStatistics statistics;
    public String getStationName(){
        if(specialName == null)
            return station.id + ". állomás";
        else return specialName;
    }
    public StationMapData(Station s, double lat, double lon, EvaluationStatistics stats){
        station = s;
        latitude = lat;
        longitude = lon;
        statistics = stats;
    }
    public void setSpecialName(String name){
        specialName = name;
    }
}
