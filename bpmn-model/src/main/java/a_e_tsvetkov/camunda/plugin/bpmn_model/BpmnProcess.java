package a_e_tsvetkov.camunda.plugin.bpmn_model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.File;
import java.util.List;

@Value
@RequiredArgsConstructor
public class BpmnProcess {
    File file;
    String id;
    List<BpmnTask> tasks;
}

