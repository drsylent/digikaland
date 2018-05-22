package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

/**
 * Egy helyet szimbolizáló osztály, melynek van egy neve, és egy részletes neve.
 */
public class Location implements Serializable {
    public String main;
    public String detailed;
    public Location(String line1, String line2){
        main = line1;
        detailed = line2;
    }
}
