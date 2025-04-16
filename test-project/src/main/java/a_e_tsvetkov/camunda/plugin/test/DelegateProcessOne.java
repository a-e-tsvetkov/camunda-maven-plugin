package a_e_tsvetkov.camunda.plugin.test;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DelegateProcessOne implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.debug("My process started");
    }
}
