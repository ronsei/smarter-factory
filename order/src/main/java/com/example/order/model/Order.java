package com.example.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class Order {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "CET")
    private Date ts;

    private String type;

    @JsonIgnore
    private String state;

    @JsonIgnore
    private String id;

    @JsonIgnore
    private Date lastUpdated;

    public Order(String id, String type) {
        this.type = type.toUpperCase();
        this.ts = new Date();
        this.id = id;
        this.lastUpdated = this.ts;
    }

}
