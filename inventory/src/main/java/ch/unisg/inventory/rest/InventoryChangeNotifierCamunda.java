package ch.unisg.inventory.rest;

import ch.unisg.inventory.model.Workpiece;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

//Note: Inventory does not know about the orders, just informs all active instances that a workpiece has been added/removed (checks if types match)
@Component
public class InventoryChangeNotifierCamunda {

    @Value("${camunda.url}")
    private String camundaUrl;

    @Value("${camunda.storage.process-definition-id}")
    private String camundaStorageProcess;

    @Value("${camunda.production.process-definition-id}")
    private String camundaProductionProcess;

    @Value("${camunda.item-added-message}")
    private String camundaAddedItemMessageReference;

    @Value("${camunda.item-removed-message}")
    private String camundaRemovedItemMessageReference;

    @Value("${camunda.variable-name.item}")
    private String camundaItemVariable;

    @Value("${camunda.storage.colormismatch}")
    private String camundaColorMismatch;

    public void notifyCamundaWorkPieceRemoved(Workpiece workpiece) {

        // 1) Get all active instances of production processes
        List<CamundaInstancesResponse> instances = retrieveCamundaInstances(camundaProductionProcess);

        // 2) For each of them, get the "type" (color) variable
        List<String> matchingInstances = getTypeMatchedInstances(instances, workpiece, camundaProductionProcess);

        // 3) Activate waiting messages in all matching instances
        activateMatchingInstances(matchingInstances, camundaRemovedItemMessageReference);
    }

    public void notifyCamundaWorkPieceAdded(Workpiece workpiece) {
        // 1) Get all active instances of storage processes
        List<CamundaInstancesResponse> instances = retrieveCamundaInstances(camundaStorageProcess);

        // 2) For each of them, get the "type" (color) variable
        List<String> matchingInstances = getTypeMatchedInstances(instances, workpiece, camundaStorageProcess);

        // 3) Activate waiting messages in all matching instances
        activateMatchingInstances(matchingInstances, camundaAddedItemMessageReference);

    }


    private List<CamundaInstancesResponse> retrieveCamundaInstances(String processDefinitionKey) {
        RestTemplate restInstances = new RestTemplate();

        String uriInstances = camundaUrl + "process-instance";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(uriInstances)
                .queryParam("processDefinitionKey", processDefinitionKey)
                .encode()
                .toUriString();

        ObjectMapper om = new ObjectMapper();
        String response = restInstances.getForObject(urlTemplate, String.class);

        List<CamundaInstancesResponse> instances = null;
        try {
            instances = om.readValue(response, new TypeReference<List<CamundaInstancesResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return instances;
    }

    private List<String> getTypeMatchedInstances(List<CamundaInstancesResponse> instances, Workpiece wp, String processDefinitionKey) {
        List<String> matchingInstances = new ArrayList<String>();
        List<String> misMatchingInstances = new ArrayList<String>();

        for (CamundaInstancesResponse instResp : instances) {
            RestTemplate restVariables = new RestTemplate();

            String uriVariables = camundaUrl + "variable-instance";
            String urlTemplateVariables = UriComponentsBuilder.fromHttpUrl(uriVariables)
                    .queryParam("processInstanceIdIn", instResp.getId())
                    .queryParam("variableName", camundaItemVariable)
                    .encode()
                    .toUriString();

            String variables = restVariables.getForObject(urlTemplateVariables, String.class);

            ObjectMapper om2 = new ObjectMapper();
            List<CamundaVariableResponse> variablesResponse = null;
            try {
                variablesResponse = om2.readValue(variables, new TypeReference<List<CamundaVariableResponse>>() { });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (variablesResponse != null) {
                for (CamundaVariableResponse variable : variablesResponse) {
                    if (variable.getValue().equals(wp.getType().toLowerCase())) {
                        matchingInstances.add(variable.getProcessInstanceId());
                    } else {
                        if (processDefinitionKey.equals(camundaStorageProcess)) {
                            misMatchingInstances.add(variable.getProcessInstanceId());
                        }
                    }
                }
                if (matchingInstances.isEmpty() && misMatchingInstances.size()==1) {

                    activateColorMismatch(misMatchingInstances.get(0));
                    return new ArrayList<String>();
                }
            }
        }
        return matchingInstances;
    }

    private void activateMatchingInstances(List<String> camundaInstances, String messageName) {
        for (String instanceId : camundaInstances) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String,String> camundaPayload = new HashMap<>();
            camundaPayload.put("messageName", messageName);
            camundaPayload.put("processInstanceId", instanceId);

            ObjectMapper objectMapper = new ObjectMapper();

            HttpEntity<String> request =
                    null;
            try {
                request = new HttpEntity<>(objectMapper.writeValueAsString(camundaPayload), httpHeaders);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String responseActivation =
                    restTemplate.postForObject(camundaUrl+"/message", request, String.class);
        }
    }

    private void activateColorMismatch(String camundaInstance) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String,String> camundaPayload = new HashMap<>();
        camundaPayload.put("messageName", camundaColorMismatch);
        camundaPayload.put("processInstanceId", camundaInstance);

        ObjectMapper objectMapper = new ObjectMapper();

        HttpEntity<String> request =
                null;
        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(camundaPayload), httpHeaders);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String responseActivation =
                restTemplate.postForObject(camundaUrl+"/message", request, String.class);
    }

}
