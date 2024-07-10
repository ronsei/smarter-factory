package ch.unisg.inventory.application;

import ch.unisg.inventory.model.Inventory;
import ch.unisg.inventory.model.Stock;
import ch.unisg.inventory.model.StockItem;
import ch.unisg.inventory.model.Workpiece;
import ch.unisg.inventory.rest.InventoryChangeNotifierCamunda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class InventoryService {

    @Autowired
    private InventoryChangeNotifierCamunda inventoryChangeNotifierCamunda;

    @Autowired
    private Inventory inventory;

    public String checkWorkpieceAvailability(String type) {
        Stock currentStock = inventory.getStock();
        if (currentStock != null) {
            for (StockItem item : currentStock.getStockItems()) {
                Workpiece wp = item.getWorkpiece();
                if (wp != null) {
                    if (wp.getType().equals(type)) {
                        return "available";
                    }
                }
            }
            return "unavailable";
        }
        return "nodata";
    }

    public Stock retrieveCurrentStock() {
        Stock stock = inventory.getStock();
        if (stock != null) {
            return inventory.getStock();
        } else {
            return new Stock();
        }
    }

    public void determineInventoryChange(ArrayList<StockItem> oldStock, ArrayList<StockItem> newStock) {

        for (int i = 0; i<oldStock.size(); i++) {
            StockItem itemOld = oldStock.get(i);
            StockItem itemNew = newStock.get(i);

            if (itemOld.getWorkpiece() == null && itemNew.getWorkpiece() != null) {
                inventoryChangeNotifierCamunda.notifyCamundaWorkPieceAdded(itemNew.getWorkpiece());
            } else {
                if (itemOld.getWorkpiece() != null && itemNew.getWorkpiece() == null) {
                    inventoryChangeNotifierCamunda.notifyCamundaWorkPieceRemoved(itemOld.getWorkpiece());
                }
            }
        }
    }

}
