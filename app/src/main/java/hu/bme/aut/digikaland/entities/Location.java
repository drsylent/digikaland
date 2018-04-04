package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

// TODO: felhasználni mindenhol, ahol csak lehet
public class Location implements Serializable {
    public String main;
    public String detailed;
    public Location(String line1, String line2){
        main = line1;
        detailed = line2;
    }
}
