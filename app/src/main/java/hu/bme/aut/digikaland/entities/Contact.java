package hu.bme.aut.digikaland.entities;

public class Contact {
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
