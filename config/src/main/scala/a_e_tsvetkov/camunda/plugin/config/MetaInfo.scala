package a_e_tsvetkov.camunda.plugin.config


import scala.collection.immutable.ArraySeq.unsafeWrapArray
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{universe => ru}

trait MetaInfo

object MetaInfo {
  def apply(s: ru.TermSymbol): MetaInfo = {
    s.info match {
      case t if t == typeOf[String] => StringMetaInfo
      case t if t.typeSymbol.fullName == typeOf[Integer].typeSymbol.fullName => IntegerMetaInfo
      case t => ObjectMetaInfo(t)
    }
  }
}

object StringMetaInfo extends MetaInfo

object IntegerMetaInfo extends MetaInfo

case class ObjectMetaInfo
(
  objectType: Type,
  ctor: Array[Any] => Any,
  members: Array[(String, MetaInfo)]
) extends MetaInfo {
  def resolve(name: String): (Int, MetaInfo) = {
    members.zipWithIndex.find(_._1._1 == name)
      .map(x => (x._2, x._1._2))
      .getOrElse(throw new RuntimeException(s"$name not found in ${members.map(_._1).mkString(", ")}"))
  }
}

object ObjectMetaInfo {
  def apply[T: TypeTag]: ObjectMetaInfo = {
    apply(typeOf[T])
  }

  def apply(t: Type): ObjectMetaInfo = {
    val rm = ru.runtimeMirror(getClass.getClassLoader)
    val clazz = t.typeSymbol.asClass
    val clazzMirror: ClassMirror = rm.reflectClass(clazz)
    val ctor = t.decl(ru.termNames.CONSTRUCTOR).asMethod
    val ctorm = clazzMirror.reflectConstructor(ctor)
    ObjectMetaInfo(t,
      args => ctorm(unsafeWrapArray(args):_*),
      ctor.paramLists.head
        .map(_.asTerm)
        .map(s => (s.name.toString, MetaInfo(s))).toArray
    )
  }
}
