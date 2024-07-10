package ch.unisg.inventory.mqtt;

import ch.unisg.inventory.application.InventoryService;
import ch.unisg.inventory.model.Inventory;
import ch.unisg.inventory.model.Stock;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class InventorySubscriber {

    @Value("${mqtt.broker}")
    private String mqttBroker;

    @Value("${mqtt.user}")
    private String mqttUser;

    @Value("${mqtt.pwd}")
    private String mqttPwd;

    @Value("${mqtt.topic}")
    private String mqttTopicInventory;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private Inventory inventory;

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
                                "inventoryClient", mqttClientFactory(), mqttTopicInventory))
                .handle(m -> {
                    try {
                        updateInventory((String) m.getPayload());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .get();
    }

    public void updateInventory(String m) throws JsonProcessingException {
        Stock oldStock = inventory.getStock();
        String fixedM = m.replace("None", "null");
        ObjectMapper om = new ObjectMapper();
        Stock newStock = om.readValue(m, Stock.class);
        inventory.setStock(newStock);

        if (oldStock != null) {
            inventoryService.determineInventoryChange(oldStock.getStockItems(), newStock.getStockItems());
        }

    }

}
