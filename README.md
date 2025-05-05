**Usage**

* Add plugin to `pom.xml`

```xml
<plugin>
    <groupId>io.github.a-e-tsvetkov.camunda-generator</groupId>
    <artifactId>maven-plugin</artifactId>
    <version>${project.version}</version>
    <configuration>
        <configFile>${project.basedir}/camunda-generator.config</configFile>
        <bpmnDirectory>${project.basedir}/src/main/resources/bpmn/</bpmnDirectory>
    </configuration>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate-model</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

* Add config file `camunda-generator.config`

```
packageName = "gen.tst"
```

**Example project**

[test-project](test-project)
