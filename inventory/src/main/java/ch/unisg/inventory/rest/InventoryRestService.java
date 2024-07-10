package ch.unisg.inventory.rest;

import ch.unisg.inventory.application.InventoryService;
import ch.unisg.inventory.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class InventoryRestService {

    @Autowired
    private InventoryService inventoryService;

    @RequestMapping(value = "check", method = RequestMethod.GET)
    public @ResponseBody boolean checkInventory(@RequestParam("type") String type) {

        String availability = inventoryService.checkWorkpieceAvailability(type.toUpperCase());

        if (availability.equals("available")) return true;
        else if (availability.equals("unavailable")) return false;
        else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @RequestMapping(value = "stock", method = RequestMethod.GET)
    public @ResponseBody Stock retrieveStock() {

        Stock stock = inventoryService.retrieveCurrentStock();

        if (stock.getStockItems() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return inventoryService.retrieveCurrentStock();
        }
    }

}
