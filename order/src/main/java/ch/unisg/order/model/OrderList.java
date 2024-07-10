package ch.unisg.order.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Getter @Setter
@Component
public class OrderList {

    private LinkedList<Order> orders;

    private LinkedList<Order> orderHistory;

    private OrderList() {
        this.orders = new LinkedList<Order>();
        this.orderHistory = new LinkedList<Order>();
    }

}
