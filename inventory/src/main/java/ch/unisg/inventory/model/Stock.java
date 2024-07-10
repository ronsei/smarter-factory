package ch.unisg.inventory.model;

import java.util.ArrayList;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor

public class Stock {

    private ArrayList<StockItem> stockItems;
    private Date ts;
}
