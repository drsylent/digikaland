package hu.bme.aut.digikaland.utility;

/**
 * Távolság számoló osztály GPS-koordináták alapján.
 */
public class DistanceCalculator {
    /**
     * Kiszámolja a távolságot méterben két GPS-koordináta között a koszinuszok gömbi törvénye szerint.
     * https://www.movable-type.co.uk/scripts/latlong.html
     * @param lat1 Az első koordináta földrajzi szélessége fokban.
     * @param lon1 Az első koordináta földrajzi magassága fokban.
     * @param lat2 A második koordináta földrajzi szélessége fokban.
     * @param lon2 A második koordináta földrajzi magassága fokban.
     * @return A távolság a két koordináta között méterben.
     */
    public static double calculate(double lat1, double lon1, double lat2, double lon2){
        int radius = 6371000;
        double latR1 = Math.toRadians(lat1);
        double latR2 = Math.toRadians(lat2);
        double deltaLon = Math.toRadians(lon2-lon1);
        return Math.acos(Math.sin(latR1)*Math.sin(latR2)+Math.cos(latR1)*Math.cos(latR2)*Math.cos(deltaLon))*radius;
    }
}
