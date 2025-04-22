package a_e_tsvetkov.camunda.plugin.config

import org.scalatest.funsuite.AnyFunSuiteLike

import scala.reflect.runtime.universe.typeOf

case class Foo(str: String, int: Integer)
case class Bar(foo: Foo)

class ObjectMetaInfoTest extends AnyFunSuiteLike {

  test("testApply") {
    val metaInfo = ObjectMetaInfo[Foo]
    assert(metaInfo.objectType == typeOf[Foo])
    assert(
      Array(
        ("str", StringMetaInfo),
        ("int", IntegerMetaInfo)
      ) === metaInfo.members)
  }

  test("testApply complex type") {
    val metaInfo = ObjectMetaInfo[Bar]
    assert(metaInfo.objectType == typeOf[Bar])
    assert(1 == metaInfo.members.length)
    assert("foo" == metaInfo.members(0)._1)
    assert(metaInfo.members(0)._2.isInstanceOf[ObjectMetaInfo])
  }
}
