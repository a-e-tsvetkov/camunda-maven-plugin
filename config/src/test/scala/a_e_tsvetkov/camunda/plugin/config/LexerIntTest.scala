package a_e_tsvetkov.camunda.plugin.config

import org.scalatest.funsuite.AnyFunSuiteLike

import scala.collection.mutable

class LexerIntTest extends AnyFunSuiteLike {

  import SToken._

  test("Parse") {
    var value = LexerInt.parse(
      """
        |# some text
        |a  = "b"
        |""".stripMargin)
    val l = mutable.Buffer[SToken]()
    while (!value.atEnd) {
      l+=value.first
      value = value.rest
    }
    assert(l.toSeq === Seq(IDENTIFIER("a"), ASSIGN, LITERAL_STRING("b")))
  }

}
