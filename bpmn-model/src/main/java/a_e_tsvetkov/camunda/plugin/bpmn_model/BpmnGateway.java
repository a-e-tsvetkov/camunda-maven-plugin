package a_e_tsvetkov.camunda.plugin.bpmn_model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class BpmnGateway {
    String id;
    Kind kind;

    public enum Kind {PARALLEL, EXCLUSIVE}
}
