package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

/**
 * Egy kapcsolattartást reprezentáló osztály, mely összeköt egy nevet és egy telefonszámot.
 */
public class Contact implements Serializable {
    // szükséges, hogy a firebase szerializálni tudjon
    public Contact(){
    }

    public Contact(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    private String name;
    private String phone;

}
