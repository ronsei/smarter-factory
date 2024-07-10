package ch.unisg.inventory.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CamundaVariableResponse {
    private String type;
    private String value;
    private ValueInfo valueInfo;
    private String id;
    private String name;
    private String processDefinitionId;
    private String processInstanceId;
    private String executionId;
    private Object caseInstanceId;
    private Object caseExecutionId;
    private Object taskId;
    private Object batchId;
    private String activityInstanceId;
    private Object errorMessage;
    private Object tenantId;

    public class ValueInfo{
    }

}

