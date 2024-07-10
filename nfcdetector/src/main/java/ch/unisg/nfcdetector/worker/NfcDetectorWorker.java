package ch.unisg.nfcdetector.worker;

import ch.unisg.nfcdetector.model.NfcReading;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;

@Component
@ExternalTaskSubscription(topicName = "nfc-detect")
public class NfcDetectorWorker implements ExternalTaskHandler {

    @Value("${mqtt.broker}")
    private String mqttBroker;

    @Value("${mqtt.user}")
    private String mqttUser;

    @Value("${mqtt.pwd}")
    private String mqttPwd;

    @Value("${mqtt.topic}")
    private String mqttTopicNfc;

    @Value("${camunda.storage.activity}")
    private String activityStorage;

    @Value("${camunda.production.activity}")
    private String activityProduction;

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
                    NfcReading reading = om.readValue(payload, NfcReading.class);

                    if (reading.getWorkpiece().getState().equals("PROCESSED")) {
                        if (externalTask.getActivityId().equals(activityProduction)) {
                            externalTaskService.complete(externalTask);
                            client.disconnect();
                        }
                    } else {
                        if (reading.getWorkpiece().getState().equals("RAW")) {
                            if (externalTask.getActivityId().equals(activityStorage)) {
                                externalTaskService.complete(externalTask);
                                client.disconnect();
                            }
                        }
                    }
                }

                public void connectionLost(Throwable cause) {
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            client.subscribe(mqttTopicNfc);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }
}
