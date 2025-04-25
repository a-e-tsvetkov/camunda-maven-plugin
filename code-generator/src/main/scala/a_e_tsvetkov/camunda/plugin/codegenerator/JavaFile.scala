package a_e_tsvetkov.camunda.plugin.codegenerator

import java.io.Writer

class JavaFile(writer: Writer) {

  var indent = 0

  def line(str: String): Unit = {
    line {
      raw(str)
    }
  }

  def line(f: => Unit): Unit = {
    for (_ <- 0 to indent) {
      writer.write("  ")
    }
    f
    writer.append("\n")
  }

  def scope(f: => Unit): Unit = {
    indent += 1
    f
    indent -= 1
  }

  private def raw(str: String): Unit = {
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
    scope {
      val length = t.values.length - 1
      t.values.zipWithIndex.foreach { case (v, i) =>
        line {
          raw(v.name)
          if (t.values.nonEmpty) {
            raw("(")
            iterate(v.args, ", ")(generate)
            raw(")")
          }
          raw(if (length == i) ";" else ",")
        }
      }
      t.fields.foreach { v =>
        generate(v)
      }
      t.methods.foreach { v =>
        generate(v)
      }
      t.ctor.foreach { v =>
        generate(t, v)
      }
    }
    line("}")
  }

  private def generate(e: JField): Unit = {
    line {
      raw("private ")
      generate(e.`type`)
      raw(" ")
      raw(e.name)
      raw(";")
    }
  }

  private def generate(t: JEnum, e: JCtor): Unit = {
    line {
      raw("private ")
      raw(t.name)
      raw("(")
      iterate(e.params, ", ")({ p =>
        generate(p.`type`)
        raw(" ")
        raw(p.name)
      })
      raw(") {")
    }
    scope {
      e.body.foreach(line)
    }
    line("}")
  }

  private def generate(m: JMethod): Unit = {
    line {
      raw("public ")
      generate(m.`type`)
      raw(" ")
      raw(m.name)
      raw("(")
      iterate(m.params, ", ") { p =>
        generate(p.`type`)
        raw(" ")
        raw(p.name)
      }
      raw(") {")
    }
    scope {
      m.body.foreach(line)
    }
    line("}")
  }

  private def generate(t: JType): Unit = {
    t match {
      case JTypeRaw(t) => raw(t)
      case JTypeRaw(t) => raw("void")
    }
  }

  private def generate(e: JExpr): Unit = {
    e match {
      case JExprStringLiteral(v) => raw("\"" + v + "\"")
    }
  }

  private def iterate[T](values: Seq[T], separator: String)(f: T => Unit): Unit = {
    val length = values.length - 1
    values.zipWithIndex.foreach { case (v, i) =>
      f(v)
      raw(if (length == i) "" else separator)
    }
  }

  private def writePackage(name: String): Unit = {
    line(s"package $name;")
  }
}
