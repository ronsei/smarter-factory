package com.example.order.rest;

import com.example.order.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

//Note: Order service owns the order, knows the related process (instance id) from the start, can actively inform Camunda
@Component
public class NotifyOrderCompleteCamundaRestService {

    @Value("${camunda.url}")
    private String camundaUrl;

    @Value("${camunda.production-ended-message}")
    private String camundaMessageReference;

    public void notifyCamunda(Order order) {

        String processInstanceId = order.getId();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String,String> camundaPayload = new HashMap<>();
        camundaPayload.put("messageName", camundaMessageReference);
        camundaPayload.put("processInstanceId", processInstanceId);

        ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request =
                null;
        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(camundaPayload), httpHeaders);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String response =
                restTemplate.postForObject(camundaUrl, request, String.class);

    }

}
