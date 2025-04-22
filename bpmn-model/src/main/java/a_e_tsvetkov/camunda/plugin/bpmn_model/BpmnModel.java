package a_e_tsvetkov.camunda.plugin.bpmn_model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
public class BpmnModel {
    List<BpmnProcess> processes;
}

