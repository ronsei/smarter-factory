package ch.unisg.order.mqtt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
public class OrderStatusMessage {

    @Getter @Setter
    private String state;

    @Getter @Setter
    private Date ts;

    @Getter @Setter
    private String type;

}
