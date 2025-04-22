package a_e_tsvetkov.camunda.plugin.codegenerator

import java.io.Writer

class JavaFile(writer: Writer) {

  def line(str: String): Unit = {
    writer.append(str).append("\n")
  }

  def line(f: () => Unit): Unit = {
    f()
    writer.append("\n")
  }

  private def raw(str: String) = {
    writer.append(str)
  }

  def generate(t: JRefType): Unit = {
    writePackage(t.packageName)
    t match {
      case e: JEnum => generate(e)
    }
  }

  private def generate(t: JEnum): Unit = {
    line(s"public enum ${t.name} {")
    iterate(t.values, ",\n", ";\n") { v =>
      raw(v.name)
      if (t.values.nonEmpty) {
        raw("(")
        iterate(v.args, ", ", "")(generate)
        raw(")")
      }
    }
    t.fields.foreach { v =>
      generate(v)
    }
    t.ctor.foreach { v =>
      generate(t, v)
    }
    line("}")
  }

  private def generate(e: JField): Unit = {
    line { () =>
      raw("private ")
      generate(e.`type`)
      raw(" ")
      raw(e.name)
      raw(";")
    }
  }

  private def generate(t: JEnum, e: JCtor): Unit = {
    line { () =>
      raw("private ")
      raw(t.name)
      raw("(")
      iterate(e.params, ", ", "") { p =>
        generate(p.`type`)
        raw(" ")
        raw(p.name)
      }
      raw(") {")
    }
    e.body.foreach(line)
    line("}")
  }

  private def generate(t: JType): Unit = {
    t match {
      case JTypeRaw(t) => raw(t)
    }
  }

  private def generate(e: JExpr): Unit = {
    e match {
      case JExprStringLiteral(v) => raw("\"" + v + "\"")
    }
  }

  private def iterate[T](values: Seq[T], separator: String, end: String)(f: T => Unit): Unit = {
    val length = values.length - 1
    values.zipWithIndex.foreach { case (v, i) =>
      f(v)
      raw(if (length == i) end else separator)
    }
  }

  private def writePackage(name: String): Unit = {
    line(s"package $name;")
  }
}
