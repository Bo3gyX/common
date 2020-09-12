package common

import play.api.libs.json._

import scala.language.implicitConversions

object OpcodeMessage {

  abstract class Opcode(val code: Int)

  object Opcode {
    case object Ping  extends Opcode(0)
    case object Pong  extends Opcode(1)
    case object Login extends Opcode(10)
    case object User  extends Opcode(11)

    val value: Seq[Opcode] = Seq(Ping, Pong, Login, User)

    def unapply(code: Int): Option[Opcode] = value.find(_.code == code)
  }

  trait Converter[T] {
    def opcode: Opcode
  }

  case class ObjectConverter[T <: Message](opcode: Opcode, obj: T) extends Converter[T]

  case class PayloadConverter[T <: Message with Payload](opcode: Opcode)(implicit format: Format[T])
    extends Converter[T] {
    def from(json: JsValue): JsResult[T] = format.reads(json)
    def to[M <: T](obj: M): JsValue      = format.writes(obj)
  }

  abstract class Message(val opcode: Opcode)

  trait Payload

  case object Ping                                  extends Message(Opcode.Ping)
  case object Pong                                  extends Message(Opcode.Pong)
  case class Login(login: String, password: String) extends Message(Opcode.Login) with Payload
  case class User(name: String, age: Int)           extends Message(Opcode.User) with Payload

  object Implicits {

    implicit val jsonOpcode: Format[Opcode] = new Format[Opcode] {
      override def writes(o: Opcode): JsValue = JsNumber(o.code)

      override def reads(json: JsValue): JsResult[Opcode] = {
        json.validate[Int].map(Opcode.unapply).flatMap {
          case Some(opcode) => JsSuccess(opcode)
          case None         => JsError("undefined opcode")
        }
      }
    }

    implicit val jsonLogin: Format[Login] = Json.format[Login]
    implicit val jsonUser: Format[User]   = Json.format[User]
  }

  def format[T <: Message with Payload: Format]: Format[T] = implicitly[Format[T]]

  implicit class OpcodeOp(val opcode: Opcode) extends AnyVal {

    def ->[T <: Message](obj: T): ObjectConverter[T] =
      ObjectConverter[T](opcode, obj)

    def ->[T <: Message with Payload](implicit frm: Format[T]): PayloadConverter[T] =
      PayloadConverter[T](opcode)
  }

  implicit def converterToTuple2[T <: Message](cnv: Converter[T]): (Opcode, Converter[T]) = (cnv.opcode, cnv)

  import Implicits._

  val mapOpcode: Map[Opcode, Converter[_ <: Message]] = Map(
    Opcode.Ping -> Ping,
    Opcode.Pong -> Pong,
    Opcode.Login -> format[Login],
    Opcode.User -> format[User]
  )

  implicit val jsonMessage: Format[Message] = new Format[Message] {

    override def writes(o: Message): JsValue = {
      mapOpcode.get(o.opcode) match {
        case Some(ObjectConverter(op, _)) =>
          Json.obj(
            "op" -> op
          )

        case Some(payload: PayloadConverter[Message]) =>
          Json.obj(
            "op" -> payload.opcode,
            "payload" -> payload.to(o)
          )

        case _ => throw new Exception("undefined message")
      }
    }

    override def reads(json: JsValue): JsResult[Message] = {
      (json \ "op").validate[Opcode].map(mapOpcode.get).flatMap {
        case Some(cnv: ObjectConverter[_])      => JsSuccess(cnv.obj)
        case Some(payload: PayloadConverter[_]) => payload.from((json \ "payload").as[JsValue])
        case _                                  => JsError("undefined message")
      }
    }

  }

  val jsonStrPing: String =
    """
      |{"op":0}
      |""".stripMargin

  val jsonsStrLogin: String =
    """
      |{"op":10,"payload":{"login":"xxx","password":"2222"}}
      |""".stripMargin

  val json: JsValue = Json.toJson[Message](Login("xxx", "2222"))
  println(json)

  val message: Message = Json.parse(jsonsStrLogin).as[Message]

  println(message)

}
