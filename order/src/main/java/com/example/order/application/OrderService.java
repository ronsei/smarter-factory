package com.example.order.application;

import com.example.order.model.Order;
import com.example.order.model.OrderList;
import com.example.order.mqtt.OrderStatusMessage;
import com.example.order.rest.NotifyOrderCompleteCamundaRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderService {

    @Autowired
    private NotifyOrderCompleteCamundaRestService notifyOrderCompleteCamundaRestService;

    @Autowired
    private OrderList orderList;

    public Order createOrder(String id, String type) {
        Order order = new Order(id, type);
        order.setState("NEW");
        orderList.getOrders().addLast(order);
        return order;

    }

    public String retrieveOrderStatus(String id) {
        for (Order order : orderList.getOrders()) {
            if (order.getId().equals(id)) {
                return order.getState();
            }
        }
        return "Order not found";
    }

    public void updateOrderStatus(OrderStatusMessage osm) {
        Order order = orderList.getOrders().getFirst();
        if (order.getType().equals(osm.getType())) { // Some simple sanity check (do the colors match?)
            order.setState(osm.getState());
            order.setLastUpdated(osm.getTs());
        }

        if (order.getState().equals("SHIPPED")) {
            Order ord = orderList.getOrders().removeFirst();
            orderList.getOrderHistory().addLast(ord);
            notifyOrderCompleteCamundaRestService.notifyCamunda(ord);
        }
    }



}
