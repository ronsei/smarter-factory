package ch.unisg.order.rest;

import ch.unisg.order.application.OrderService;
import ch.unisg.order.model.Order;
import ch.unisg.order.mqtt.OrderSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OrderRestService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderSender orderSender;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "neworder")
    public void sendNewOrder(@RequestBody Map<String,String> payload) {
        String type = payload.get("type");
        String processInstanceId = payload.get("processInstanceId");
        Order order = orderService.createOrder(processInstanceId, type);
        orderSender.publishNewOrder(order);
    }

    @RequestMapping(value = "orderstatus", method = RequestMethod.GET)
    public @ResponseBody String retrieveOrderStatus(@RequestParam("id") String orderId) {
        return orderService.retrieveOrderStatus(orderId);
    }

}
