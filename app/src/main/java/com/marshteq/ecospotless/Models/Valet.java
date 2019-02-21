package com.marshteq.ecospotless.Models;

public class Valet {
    public String id;
    public String name;
    public String surname;
    public String contact_number;
    public String address;
    public String email;

    public Valet(String id, String name, String surname,String email,String contact_number,String address){
        this.id  = id;
        this.name = name;
        this.surname = surname;
        this.contact_number = contact_number;
        this.address = address;
        this.email = email;
    }
}
