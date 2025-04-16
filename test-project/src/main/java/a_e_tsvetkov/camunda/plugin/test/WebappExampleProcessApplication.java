package a_e_tsvetkov.camunda.plugin.test;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class WebappExampleProcessApplication {

  public static void main(String... args) {
    SpringApplication.run(WebappExampleProcessApplication.class, args);
  }
}
