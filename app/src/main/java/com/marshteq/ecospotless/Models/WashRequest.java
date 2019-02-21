package com.marshteq.ecospotless.Models;

public class WashRequest {
    public String id;
    public Client client;
    public Price price;
    public String description;
    public Franchisee franchisee;
    public String wash_date;
    public String wash_time;
    public String created_at;
    public String updated_at,status;
    public Valet valet;
    public String wash_location;

    public WashRequest(String id, Client client, Price price, String description, Franchisee franchisee, Valet valet,String wash_date, String wash_time,String status,String wash_location, String created_at, String updated_at) {
        this.id = id;
        this.client = client;
        this.valet = valet;
        this.price = price;
        this.description = description;
        this.updated_at = updated_at;
        this.created_at = created_at;
        this.franchisee = franchisee;
        this.wash_date = wash_date;
        this.wash_time = wash_time;
        this.status = status;
    }
}
