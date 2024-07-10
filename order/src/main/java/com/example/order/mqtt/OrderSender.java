package com.example.order.mqtt;

import com.example.order.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderSender {

    @Value("${mqtt.broker}")
    private String mqttBroker;

    @Value("${mqtt.user}")
    private String mqttUser;

    @Value("${mqtt.pwd}")
    private String mqttPwd;

    @Value("${mqtt.topic-sending}")
    private String mqttTopicSendOrder;

    public void publishNewOrder(Order order) {

        String json = "";
        ObjectMapper ow = new ObjectMapper();
        try {
            json = ow.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            IMqttClient publisher = new MqttClient(mqttBroker,"OrderSenderService");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttUser);
            options.setPassword(mqttPwd.toCharArray());
            options.setConnectionTimeout(10);
            publisher.connect(options);
            publisher.publish(mqttTopicSendOrder,new MqttMessage(json.getBytes()));
            publisher.disconnect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }

}
