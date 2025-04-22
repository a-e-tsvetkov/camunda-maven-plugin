package a_e_tsvetkov.camunda.plugin.codegenerator

import a_e_tsvetkov.camunda.plugin.bpmn_model.BpmnModel
import a_e_tsvetkov.camunda.plugin.config.Config

import scala.jdk.CollectionConverters.CollectionHasAsScala

case class BpmnTransformer(config: Config) extends JavaTransformer[BpmnModel] {
  override def map(model: BpmnModel): Seq[JRefType] = {
    val tasks = model.getProcesses
      .asScala
      .flatMap(_.getTasks.asScala)
      .toSeq

    Seq(
      JEnum(
        packageName = config.packageName,
        name = "ProcessTasks",
        values = tasks.map(x => JEnumValue(x.getId, Seq(JExprStringLiteral(x.getId)))),
        fields = Seq(JField("id", JTypeRaw("String"))),
        ctor = Some(JCtor(
          params = Seq(JParam("id", JTypeRaw("String"))),
          body = Seq("this.id = id;"))
        ))
    )
  }
}
