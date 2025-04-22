package a_e_tsvetkov.camunda.plugin;

import a_e_tsvetkov.camunda.plugin.codegenerator.JavaBpmnGenerator;
import a_e_tsvetkov.camunda.plugin.config.ConfigParser;
import a_e_tsvetkov.camunda.plugin.modelparser.BpmnModelParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.VALIDATE)
public class CamundaGeneratorMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "configFile", required = true)
    File configFile;
    @Parameter(property = "bpmnDirectory", required = true)
    File bpmnDirectory;
    @Parameter(property = "outputFolder", defaultValue = "${project.build.directory}/generated-sources/camunda")
    File outputFolder;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("executing CamundaGeneratorMojo.execute()");
        getLog().debug("configFile: " + configFile);
        getLog().debug("configFile: " + configFile.exists());
        getLog().debug("bpmnDirectory: " + bpmnDirectory);
        getLog().debug("bpmnDirectory: " + bpmnDirectory.exists());
        getLog().debug("outputFolder: " + outputFolder);
        getLog().debug("outputFolder: " + outputFolder.exists());
        if (!configFile.exists()) {
//            getLog().error("configFile doesn't exists: " + configFile);
            throw new MojoExecutionException("configFile doesn't exists: " + configFile);
        }

        var config = ConfigParser.parse(configFile);
        var bpmnModel = BpmnModelParser.parse(bpmnDirectory);
        JavaBpmnGenerator.create(outputFolder, bpmnModel, config);
        project.addCompileSourceRoot(outputFolder.getPath());
    }
}
