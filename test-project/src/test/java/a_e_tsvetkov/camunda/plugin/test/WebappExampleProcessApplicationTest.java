package a_e_tsvetkov.camunda.plugin.test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest
@Slf4j
class WebappExampleProcessApplicationTest {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;

    @Test
    void runBusinessProcess() {
        String businessKey = UUID.randomUUID().toString();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("DummyProcess", businessKey);

        waitFor(processInstance, "End");
    }

    @SneakyThrows
    private void waitFor(ProcessInstance processInstance, String taskId) {
        Instant start = Instant.now();
        Instant end = start.plus(30, ChronoUnit.SECONDS);
        while (Instant.now().isBefore(end)) {
            var list = historyService.createHistoricActivityInstanceQuery()
                    .activityId(taskId)
                    .processInstanceId(processInstance.getProcessInstanceId())
                    .list();
            if (list.size() == 1) {
                log.debug("Arrived at {}", list.get(0));
                return;
            }
            Thread.sleep(10);
        }
        fail(MessageFormat.format("Task {0} is not reached", taskId));
    }
}
