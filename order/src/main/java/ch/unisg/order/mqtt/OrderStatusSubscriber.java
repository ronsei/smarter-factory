package ch.unisg.order.mqtt;

import ch.unisg.order.application.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusSubscriber {

    @Value("${mqtt.broker}")
    private String mqttBroker;

    @Value("${mqtt.user}")
    private String mqttUser;

    @Value("${mqtt.pwd}")
    private String mqttPwd;

    @Value("${mqtt.topic-status}")
    private String mqttTopicOrderStatus;

    @Autowired
    private OrderService orderService;

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{ mqttBroker });
        options.setUserName(mqttUser);
        options.setPassword(mqttPwd.toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public IntegrationFlow mqttInbound() {
        return IntegrationFlow.from(
                        new MqttPahoMessageDrivenChannelAdapter(mqttBroker,
                                "orderClient", mqttClientFactory(), mqttTopicOrderStatus))
                .handle(m -> {
                    try {
                        updateOrderStatus((String) m.getPayload());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .get();
    }

    public void updateOrderStatus(String m) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        OrderStatusMessage osm = om.readValue(m, OrderStatusMessage.class);
        orderService.updateOrderStatus(osm);
    }

}
