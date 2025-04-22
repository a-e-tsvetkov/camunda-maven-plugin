package a_e_tsvetkov.camunda.plugin.modelparser

import a_e_tsvetkov.camunda.plugin.bpmn_model.{BpmnModel, BpmnProcess, BpmnTask}
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.impl.BpmnParser
import org.camunda.bpm.model.bpmn.instance.{Process, ServiceTask}

import java.io.{File, FileInputStream, FileNotFoundException}
import scala.jdk.CollectionConverters._
import scala.jdk.javaapi.CollectionConverters.asJava


object BpmnModelParser {
  def parse(bpmnDirectory: File): BpmnModel = {
    val list = bpmnDirectory.listFiles((_, n) => n.endsWith(".bpmn"))
      .flatMap(parseBpmnFile)
      .toSeq
    new BpmnModel(asJava(list))
  }

  def parseBpmnFile(file: File): Seq[BpmnProcess] = try {
    val inputStream = new FileInputStream(file)
    val parser = new BpmnParser
    val model = parser.parseModelFromStream(inputStream)
    convert(file, model)
  } catch {
    case e: FileNotFoundException =>
      throw new RuntimeException(e)
  }

  private def convert(file: File, modelInstance: BpmnModelInstance): Seq[BpmnProcess] = {
    modelInstance
      .getDefinitions
      .getRootElements
      .asScala
      .toSeq
      .map(_.asInstanceOf[Process])
      .map(x => convert(file, x))
  }

  private def convert(file: File, process: Process): BpmnProcess = {
    val tasks = process.getFlowElements
      .asScala
      .toSeq
      .collect({ case x: ServiceTask => new BpmnTask(x.getId) })
    new BpmnProcess(file, process.getId, tasks.asJava)
  }
}
