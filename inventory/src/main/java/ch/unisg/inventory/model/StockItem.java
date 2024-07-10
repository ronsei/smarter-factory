package ch.unisg.inventory.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor

public class StockItem {
    private String location;
    private Workpiece workpiece;
}
