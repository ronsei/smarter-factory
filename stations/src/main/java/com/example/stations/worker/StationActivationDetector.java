package com.example.stations.worker;

import com.example.stations.model.StationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ExternalTaskSubscription(topicName = "station-detect")
public class StationActivationDetector implements ExternalTaskHandler {

    @Value("${mqtt.broker}")
    private String mqttBroker;

    @Value("${mqtt.user}")
    private String mqttUser;

    @Value("${mqtt.pwd}")
    private String mqttPwd;

    @Value("${mqtt.topic}")
    private String mqttTopic;

    @Value("${camunda.activity.dsi}")
    private String activityDsi;

    @Value("${camunda.activity.mpo}")
    private String activityMpo;

    @Value("${camunda.activity.dso}")
    private String activityDso;

    @Value("${camunda.activity.vgr}")
    private String activityVgr;

    @Value("${camunda.activity.sld}")
    private String activitySld;

    @Value("${camunda.activity.hbw}")
    private String activityHbw;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        UUID uuid = UUID.randomUUID();
        String clientId = uuid.toString();

        try {
            MqttClient client = new MqttClient(mqttBroker, clientId, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttUser);
            options.setPassword(mqttPwd.toCharArray());
            client.connect(options);
            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    String payload = new String(message.getPayload());

                    ObjectMapper om = new ObjectMapper();
                    StationMessage reading = om.readValue(payload, StationMessage.class);

                    String station = reading.getStation();

                    if (reading.getActive()==1) {
                        switch(station) {
                            case "mpo":
                                if (externalTask.getActivityId().equals(activityMpo)) {
                                    externalTaskService.complete(externalTask);
                                    client.disconnect();
                                } else break;
                            case "dsi":
                                if (externalTask.getActivityId().equals(activityDsi)) {
                                    externalTaskService.complete(externalTask);
                                    client.disconnect();
                                } else break;
                            case "dso":
                                if (externalTask.getActivityId().equals(activityDso)) {
                                    externalTaskService.complete(externalTask);
                                    client.disconnect();
                                } else break;
                            case "vgr":
                                if (externalTask.getActivityId().equals(activityVgr)) {
                                    externalTaskService.complete(externalTask);
                                    client.disconnect();
                                } else break;
                            case "hbw":
                                if (externalTask.getActivityId().equals(activityHbw)) {
                                    externalTaskService.complete(externalTask);
                                    client.disconnect();
                                } else break;
                            case "sld":
                                if (externalTask.getActivityId().equals(activitySld)) {
                                    externalTaskService.complete(externalTask);
                                    client.disconnect();
                                } else break;
                        }
                    }

                }

                public void connectionLost(Throwable cause) {
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            client.subscribe(mqttTopic);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }


    }
}
