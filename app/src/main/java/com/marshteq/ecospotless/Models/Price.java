package com.marshteq.ecospotless.Models;

public class Price {
    public String id;
    public String service;
    public String description;
    public String vehichle_type_id;
    public double price;
    public String service_type;
    public Vehicle vehicle;

    public Price(String id,String service,String description, String vehichle_type_id,double price,String service_type,Vehicle vehicle){
        this.id = id;
        this.service = service;
        this.vehichle_type_id = vehichle_type_id;
        this.description = description;
        this.price = price;
        this.service_type = service_type;
        this.vehicle = vehicle;
    }
}
