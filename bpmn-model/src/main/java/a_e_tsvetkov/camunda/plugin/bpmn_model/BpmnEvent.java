package a_e_tsvetkov.camunda.plugin.bpmn_model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class BpmnEvent {
    String id;
    Kind kind;

    public enum Kind {START, END}
}
