package ch.unisg.inventory.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor
public class CamundaInstancesResponse {

    private ArrayList<Object> links;
    private String id;
    private String definitionId;
    private Object businessKey;
    private Object caseInstanceId;
    private boolean ended;
    private boolean suspended;
    private Object tenantId;

}
