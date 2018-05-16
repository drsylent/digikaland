package hu.bme.aut.digikaland.utility;

public class DistanceCalculator {
    // méterben
    // spherical law of cosines
    // https://www.movable-type.co.uk/scripts/latlong.html
    public static double calculate(double lat1, double lon1, double lat2, double lon2){
        int radius = 6371000;
        double latR1 = Math.toRadians(lat1);
        double latR2 = Math.toRadians(lat2);
        double deltaLon = Math.toRadians(lon2-lon1);
        return Math.acos(Math.sin(latR1)*Math.sin(latR2)+Math.cos(latR1)*Math.cos(latR2)*Math.cos(deltaLon))*radius;
    }
}
