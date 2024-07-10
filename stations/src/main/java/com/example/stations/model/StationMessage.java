package com.example.stations.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationMessage {

    private int active;
    private int code;
    private String description;
    private String station;
    private String target;
    private Date ts;

}
