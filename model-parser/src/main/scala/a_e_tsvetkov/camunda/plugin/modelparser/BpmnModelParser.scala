package a_e_tsvetkov.camunda.plugin.modelparser

import a_e_tsvetkov.camunda.plugin.bpmn_model.{BpmnEvent, BpmnGateway, BpmnModel, BpmnProcess, BpmnTask}
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.impl.BpmnParser
import org.camunda.bpm.model.bpmn.instance.{EndEvent, ExclusiveGateway, Gateway, ParallelGateway, Process, ServiceTask, StartEvent, Task}

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
      .collect {
        case x: Process => x
      }
      .map(x => convert(file, x))
  }

  private def convert(file: File, process: Process): BpmnProcess = {
    val elements = process.getFlowElements
      .asScala
      .toSeq
    elements
      .foreach({ x =>

        println(s"id=${x.getId} class=${x.getClass.getCanonicalName}")
      })
    val tasks = elements
      .collect({
        case x: Task => new BpmnTask(x.getId)
      })
    val events = elements
      .collect({
        case x: StartEvent => new BpmnEvent(x.getId, BpmnEvent.Kind.START)
        case x: EndEvent => new BpmnEvent(x.getId, BpmnEvent.Kind.END)
      })
    val gateways = elements
      .collect({
        case x: ParallelGateway => new BpmnGateway(x.getId, BpmnGateway.Kind.PARALLEL)
        case x: ExclusiveGateway => new BpmnGateway(x.getId, BpmnGateway.Kind.EXCLUSIVE)
      })
    new BpmnProcess(
      file,
      process.getId,
      tasks.asJava,
      events.asJava,
      gateways.asJava
    )
  }
}
