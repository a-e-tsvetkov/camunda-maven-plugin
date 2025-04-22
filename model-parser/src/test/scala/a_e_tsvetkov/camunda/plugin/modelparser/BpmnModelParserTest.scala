package a_e_tsvetkov.camunda.plugin.modelparser

import a_e_tsvetkov.camunda.plugin.bpmn_model.BpmnProcess
import a_e_tsvetkov.camunda.plugin.config.{IntegerMetaInfo, StringMetaInfo}
import org.scalatest.funsuite.AnyFunSuiteLike

import java.io.File
import scala.jdk.CollectionConverters.ListHasAsScala

class BpmnModelParserTest extends AnyFunSuiteLike {
  test("testApply") {
    val processes = BpmnModelParser.parseBpmnFile(loadBpmn("dummy.bpmn"))
    assert(
      processes.map(_.getId) ===
        Seq(
          "DummyProcess"
        ))
    val process: BpmnProcess = processes.head
    assert(
      process.getTasks.asScala.toSeq.map(_.getId) ===
        Seq(
          "Process_One"
        ))
  }

  private def loadBpmn(fileName: String): File = {
    new File(getClass.getClassLoader
      .getResource(fileName)
      .getFile)
  }
}
