package a_e_tsvetkov.camunda.plugin.config

import org.scalatest.funsuite.AnyFunSuiteLike

class ConfigParserTest extends AnyFunSuiteLike {

  test("testParse") {
    val config = ConfigParser.parse(
      """
        |packageName = "my.package"
        |""".stripMargin)

    assert(config.packageName == "my.package")
  }
}
