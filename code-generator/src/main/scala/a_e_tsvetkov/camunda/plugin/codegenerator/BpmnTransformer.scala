package a_e_tsvetkov.camunda.plugin.codegenerator

import a_e_tsvetkov.camunda.plugin.bpmn_model.BpmnModel
import a_e_tsvetkov.camunda.plugin.config.Config

import scala.jdk.CollectionConverters.CollectionHasAsScala

case class BpmnTransformer(config: Config) extends JavaTransformer[BpmnModel] {
  override def map(model: BpmnModel): Seq[JRefType] = {
    val processes = model.getProcesses.asScala
    val tasks = processes
      .flatMap(_.getTasks.asScala)
      .toSeq
      .map(_.getId)
    val events = processes
      .flatMap(_.getEvents.asScala)
      .toSeq
      .map(_.getId)
    val gateways = processes
      .flatMap(_.getGateways.asScala)
      .toSeq
      .map(_.getId)

    val enumValues = tasks ++ events ++ gateways
    val idType = JTypeRaw("String")
    Seq(
      JEnum(
        packageName = config.packageName,
        name = "ProcessTasks",
        values = enumValues.map(x => JEnumValue(x, Seq(JExprStringLiteral(x)))),
        fields = Seq(JField("id", idType)),
        ctor = Some(JCtor(
          params = Seq(JParam("id", idType)),
          body = Seq("this.id = id;"))
        ),
        methods = Seq(JMethod(
          name = "getId",
          params = Seq(),
          `type` = idType,
          body = Seq("return this.id;"))
        ))
    )
  }
}
