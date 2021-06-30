package common

import _root_.util.AkkaApp
import io.circe.Decoder.{decodeOption, Result}
import io.circe._
import io.circe.derivation.{deriveCodec, renaming}
import io.circe.syntax.EncoderOps

object Main extends AkkaApp("Common") {

  trait Payload[+T] {
    def opcode: Operation.Code
    def data: Option[T]
    def s: Option[Int]
    def event: Event.Name
  }

  object Payload {
    case class Id(opcode: Operation.Code, event: Event.Name)

    case class Raw[T](opcode: Operation.Code, data: Option[T], s: Option[Int], event: Event.Name) extends Payload[T]

    def apply(opcode: Operation.Code): Payload[Nothing] =
      Raw(opcode, None, None, Event.None)

    def apply[T](opcode: Operation.Code with Entity[T], entity: T): Payload[T] =
      Raw(opcode, Some(entity), None, Event.None)

    def apply[T](event: Event.Name with Entity[T], entity: T): Payload[T] =
      Raw(Operation.Dispatch, Some(entity), None, event)
  }

  trait Entity[T]

  object Entity {
    case class Hello(heartbeatInterval: Int)
    case class Ready(sessionId: String)
  }

  trait Operation

  object Operation {
    sealed abstract class Code(val value: Int)

    case object Dispatch     extends Code(0)
    case object Hello        extends Code(10) with Entity[Entity.Hello]
    case object Heartbeat    extends Code(1) with Entity[Int]
    case object HeartbeatAck extends Code(11)

    val values: Seq[Code]              = Seq(Dispatch, Hello, Heartbeat, HeartbeatAck)
    def find(value: Int): Option[Code] = values.find(_.value == value)
  }

  trait Event

  object Event {
    sealed abstract class Name(val value: String)

    case object None  extends Name("")
    case object Ready extends Name("READY") with Entity[Entity.Ready]

    val values: Seq[Name]                 = Seq(None, Ready)
    def find(value: String): Option[Name] = values.find(_.value == value)
  }

  implicit val codecOperationCode: Codec[Operation.Code] = new Codec[Operation.Code] {

    override def apply(c: HCursor): Result[Operation.Code] =
      for {
        value <- c.as[Int]
        code  <- Operation.find(value).toRight(DecodingFailure(s"Undefined code $value", c.history))
      } yield code

    override def apply(a: Operation.Code): Json = a.value.asJson
  }

  implicit val codeEventName: Codec[Event.Name] = new Codec[Event.Name] {

    override def apply(c: HCursor): Result[Event.Name] =
      for {
        value <- c.as[Option[String]]
        name <- value match {
          case Some(v) => Event.find(v).toRight(DecodingFailure(s"Undefined name $v", c.history))
          case None    => Right(Event.None)
        }
      } yield name

    override def apply(a: Event.Name): Json =
      (a match {
        case Event.None => None
        case name       => Some(name.value)
      }).asJson
  }

  implicit val codecHello: Codec[Entity.Hello] = deriveCodec[Entity.Hello](renaming.snakeCase)
  implicit val codecReady: Codec[Entity.Ready] = deriveCodec[Entity.Ready](renaming.snakeCase)

  implicit def encoderPayload: Encoder[Payload[Any]] = new Encoder[Payload[Any]] {

    override def apply(a: Payload[Any]): Json = {
      val id    = Payload.Id(a.opcode, a.event)
      val codec = codecs(id).map(_.asInstanceOf[Encoder[Any]])
      val data = for {
        c <- codec
        d <- a.data
      } yield d.asJson(c)

      Json.obj(
        "op" -> a.opcode.asJson,
        "d" -> data.asJson,
        "s" -> a.s.asJson,
        "t" -> a.event.asJson
      )
    }
  }

  implicit def decoderPayload: Decoder[Payload[Any]] = new Decoder[Payload[Any]] {

    override def apply(c: HCursor): Result[Payload[Any]] = {
      for {
        opcode <- c.downField("op").as[Operation.Code]
        event  <- c.downField("t").as[Event.Name]
        id      = Payload.Id(opcode, event)
        decoder = codecs(id).map(_.asInstanceOf[Decoder[Any]])
        data <- if (decoder.isEmpty) Right(None) else c.downField("d").as[Any](decoder.get).map(Option(_))
        seq  <- c.downField("s").as[Option[Int]]
      } yield Payload.Raw[Any](opcode, data, seq, event)
    }
  }

  var codecs: Map[Payload.Id, Option[Codec[_]]] = Map.empty

  def register(even: Event.Name): Unit = {
    val id = Payload.Id(Operation.Dispatch, even)
    codecs = codecs ++ Map(id -> None)
  }

  def register(opcode: Operation.Code): Unit = {
    val id = Payload.Id(opcode, Event.None)
    codecs = codecs ++ Map(id -> None)
  }

  def register[T: Encoder: Decoder](even: Event.Name with Entity[T]): Unit = register[T](Operation.Dispatch, even)

  def register[T: Encoder: Decoder](opcode: Operation.Code with Entity[T]): Unit = register[T](opcode, Event.None)

  def register[T: Encoder: Decoder](opcode: Operation.Code, event: Event.Name): Unit = {
    val decoder = implicitly[Decoder[T]]
    val encoder = implicitly[Encoder[T]]
    val codec   = Codec.from(decoder, encoder)
    val id      = Payload.Id(opcode, event)
    codecs = codecs ++ Map(id -> Some(codec))
  }

  register(Operation.Hello)
  register(Event.Ready)
  register(Operation.Heartbeat)
  register(Operation.HeartbeatAck)

  val p1 = Payload(Operation.Hello, Entity.Hello(100500))
  val p2 = Payload(Event.Ready, Entity.Ready("xxx-yyy-zzz"))
  val p3 = Payload(Operation.Heartbeat, 500100)
  val p4 = Payload(Operation.HeartbeatAck)

  def asAny[T](payload: Payload[T]): Payload[Any] = payload.asInstanceOf[Payload[Any]]

  val json = asAny(p1).asJson
  println(json)
  val payload = json.as[Payload[Any]].getOrElse(throw new RuntimeException)
  println(payload)

  processing(payload)

  def processing(payload: Payload[Any]): Unit = payload match {
    case Payload.Raw(_, Some(e: Entity.Hello), _, _)          => println(s"Hello: ${e.heartbeatInterval}")
    case Payload.Raw(_, Some(e: Entity.Ready), _, _)          => println(s"Ready: ${e.sessionId}")
    case Payload.Raw(Operation.Heartbeat, Some(e: Int), _, _) => println(s"Heartbeat: $e")
    case Payload.Raw(Operation.HeartbeatAck, _, _, _)         => println(s"HeartbeatAck")

  }

}
