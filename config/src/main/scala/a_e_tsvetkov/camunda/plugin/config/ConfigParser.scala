package a_e_tsvetkov.camunda.plugin.config


import java.io.File
import java.nio.file.Files
import java.util
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.reflect.runtime.universe._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.combinator.lexical.Scanners
import scala.util.parsing.input.CharArrayReader.EofCh
import scala.util.parsing.input.{CharSequenceReader, Reader}

object ConfigParser {
  def parse(configFile: File): Config =
    ConfigParserInternal.parse[Config](ParseUnit(configFile))

  def parse(content: String): Config =
    ConfigParserInternal.parse[Config](ParseUnit(content))
}

case class ParseUnit(location: String, content: String)

object ParseUnit {
  def apply(configFile: File): ParseUnit = {
    val strings: util.List[String] = Files.readAllLines(configFile.toPath)
    val content = strings
      .asScala
      .mkString("\n")
    new ParseUnit(configFile.getPath, content)
  }

  def apply(content: String): ParseUnit = {
    new ParseUnit("inmemory", content)
  }
}

object ConfigParserInternal extends Parsers {

  import SToken._

  override type Elem = SToken

  def goal[T](objectMetaInfo: ObjectMetaInfo): Parser[T] = phrase(file(objectMetaInfo))

  def file[T](objectMetaInfo: ObjectMetaInfo): Parser[T] =
    objectBody(objectMetaInfo).map(_.asInstanceOf[T])

  def objectBody(objectMetaInfo: ObjectMetaInfo): Parser[Any] =
    rep(objectField(objectMetaInfo)) ^^ { list =>
      val values = objectMetaInfo.members
        .map(x => list.find(_._1 == x._1))
        .map(x => x.map(_._2).get)
      objectMetaInfo.ctor(values)
    }

  def objectField(objectMetaInfo: ObjectMetaInfo): Parser[(String, Any)] =
    (propertyName <~ elem(ASSIGN)) >> { name =>
      val t = objectMetaInfo.resolve(name)
      propertyValue(t._2).map((name, _))
    }

  def propertyName: Parser[String] =
    accept("property_name", { case IDENTIFIER(v) => v })

  def propertyValue(metaInfo: MetaInfo): Parser[Any] = {
    metaInfo match {
      case StringMetaInfo => propertyValueString
      case _ => throw new NotImplementedError()
    }
  }

  def propertyValueString: Parser[String] =
    accept("string_literal", { case LITERAL_STRING(v) => v })

  def parse[T: TypeTag](configFile: ParseUnit): T = {
    val tokens = LexerInt.parse(configFile.content)
    val value: ParseResult[T] = goal(ObjectMetaInfo[T])(tokens)
    value match {
      case Success(st, _) => st
      case NoSuccess(msg, _) => throw new RuntimeException(msg)
    }
  }
}

object LexerInt extends Scanners {

  private def letter = elem("letter", _.isLetter)

  private def digit = elem("digit", _.isDigit)

  private def chrExcept(cs: Char*) = elem("![" + cs.mkString("") + "]", ch => !cs.contains(ch))

  private def whitespaceChar = elem("space char", ch => ch <= ' ' && ch != EofCh)

  import SToken._

  override type Token = SToken

  private val numericLiteral =
    rep1(digit) ^^ (txt => LITERAL_NUMERIC(txt.mkString("").toLong))

  private val stringLiteral =
    ('\"' ~> rep(chrExcept('"')) <~ '"') ^^ { s => LITERAL_STRING(s.mkString("")) }

  private val literal = numericLiteral | stringLiteral

  private val assign =
    '=' ^^^ ASSIGN

  private val brace =
    '{' ^^^ BRACE_LEFT |
      '}' ^^^ BRACE_RIGHT

  private val bracket =
    '[' ^^^ BRACKET_LEFT |
      ']' ^^^ BRACKET_RIGHT

  private val separator =
    ',' ^^^ COMMA |
      ':' ^^^ COLON

  private val keyword: Map[String, SToken] = Map(
    //    "byte" -> BYTE
  )

  private val identifierOrKeyword = letter ~ rep(letter | digit | '_') ^^ {
    case x ~ xs =>
      val ident = x :: xs mkString ""
      keyword.getOrElse(ident, IDENTIFIER(ident))
  }

  override def whitespace: Parser[Any] = rep[Any](
    whitespaceChar
      | '#' ~ rep(chrExcept(EofCh, '\n'))
  )


  override def token: Parser[Token] =
    identifierOrKeyword |
      literal |
      assign |
      brace |
      bracket |
      separator |
      failure("Unknown token")


  override def errorToken(msg: String): Token = ERROR(msg)

  def parse(sqlText: String): Reader[Token] = {
    val reader = new CharSequenceReader(sqlText)
    new Scanner(reader)
  }
}

sealed trait SToken

object SToken {

  case class ERROR(msg: String) extends SToken

  case class IDENTIFIER(value: String) extends SToken

  case class LITERAL_STRING(value: String) extends SToken

  case class LITERAL_NUMERIC(value: Long) extends SToken

  case object COMMA extends SToken

  case object COLON extends SToken

  case object BRACE_LEFT extends SToken

  case object BRACE_RIGHT extends SToken

  case object BRACKET_LEFT extends SToken

  case object BRACKET_RIGHT extends SToken

  case object ASSIGN extends SToken

  //Keywords
}
