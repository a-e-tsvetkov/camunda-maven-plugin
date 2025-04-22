package a_e_tsvetkov.camunda.plugin.codegenerator

sealed trait JRefType {
  val packageName: String
  val name: String
}

case class JClass(packageName: String, name: String) extends JRefType

case class JEnum
(
  packageName: String,
  name: String,
  values: Seq[JEnumValue],
  fields: Seq[JField],
  ctor: Option[JCtor]
) extends JRefType

case class JCtor(params: Seq[JParam], body: Seq[String])

case class JParam(name: String, `type`: JType)

case class JField(name: String, `type`: JType)

case class JEnumValue(name: String, args: Seq[JExpr])

sealed trait JType

case class JTypeRaw(text: String) extends JType

sealed trait JExpr

case class JExprStringLiteral(value: String) extends JExpr
