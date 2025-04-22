package a_e_tsvetkov.camunda.plugin.codegenerator

import java.io.{File, FileWriter}
import java.nio.file.Path
import scala.util.Using

trait JavaTransformer[T] {

  def map(model: T): Seq[JRefType]

}

class JavaGenerator[T](dir: File, transformer: JavaTransformer[T]) {
  def generate(model: T): Unit = {
    val j = transformer.map(model)

    j.foreach(generate)
  }

  private def generate(t: JRefType): Unit = {
    val folder = toPath(dir.toPath, t.packageName)
    folder.toFile.mkdirs()
    val file = folder.resolve(t.name + ".java")
    Using(new FileWriter(file.toFile)) { writer =>
      val javaFile = new JavaFile(writer)
      javaFile.generate(t)
    }
  }

  private def toPath(root: Path, packageName: String): Path =
    packageName.split('.')
      .foldLeft(root) { (a, b) => a.resolve(b) }

}
