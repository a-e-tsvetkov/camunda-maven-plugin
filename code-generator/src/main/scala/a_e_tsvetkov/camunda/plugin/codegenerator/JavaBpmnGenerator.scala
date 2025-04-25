package a_e_tsvetkov.camunda.plugin.codegenerator

import a_e_tsvetkov.camunda.plugin.bpmn_model.BpmnModel
import a_e_tsvetkov.camunda.plugin.config.Config

import java.io.File

object JavaBpmnGenerator {
  def create(basedir: File, model: BpmnModel, config: Config): Unit = {
    println("bpmnModel = " + model)

    val g = new JavaGenerator(basedir, BpmnTransformer(config))
    g.generate(model)
  }
}
