package common

import _root_.util.AkkaApp
import io.circe.Decoder.{decodeOption, Result}
import io.circe.derivation.{deriveCodec, renaming}
import io.circe.syntax.EncoderOps
import io.circe._

object Main extends AkkaApp("Common") {

  case class Payload[T](op: Int, d: Option[T], s: Option[Int], t: Option[String])

  object Payload {
    case class Id[O, E](opcode: Operation.Code[O], event: Event.Name[E])

    case class Hello(heartbeatInterval: Int)
    case class Ready(sessionId: String)
  }

  trait Operation

  object Operation {
    sealed abstract class Code[+T](val value: Int)

    case object Dispatch     extends Code[Nothing](0)
    case object Hello        extends Code[Payload.Hello](10)
    case object Heartbeat    extends Code[Int](1)
    case object HeartbeatAck extends Code[Nothing](11)

    val values: Seq[Code[_]]                 = Seq(Dispatch, Hello, Heartbeat, HeartbeatAck)
    def find[T](value: Int): Option[Code[T]] = values.find(_.value == value).map(_.asInstanceOf[Code[T]])
  }

  trait Event

  object Event {
    sealed abstract class Name[+T](val value: String)

    case object None  extends Name[Nothing]("")
    case object Ready extends Name[Payload.Ready]("READY")

    val values: Seq[Name[_]]                    = Seq(None, Ready)
    def find[T](value: String): Option[Name[T]] = values.find(_.value == value).map(_.asInstanceOf[Name[T]])
  }

  implicit val codecHello: Codec[Payload.Hello] = deriveCodec[Payload.Hello](renaming.snakeCase)
  implicit val codecReady: Codec[Payload.Ready] = deriveCodec[Payload.Ready](renaming.snakeCase)

  implicit def encoderPayload[T]: Encoder[Payload[T]] = new Encoder[Payload[T]] {

    override def apply(a: Payload[T]): Json = {
      val needOpcode = Operation.find[T](a.op).get
      val needEvent  = a.t.flatMap(Event.find[T]).getOrElse(Event.None)
      val id         = Payload.Id(needOpcode, needEvent)
      val codec      = codecs(id)
      val data = for {
        c <- codec.map(_.asInstanceOf[Encoder[T]])
        d <- a.d
      } yield d.asJson(c)

      Json.obj(
        "op" -> a.op.asJson,
        "d" -> data.asJson,
        "s" -> a.s.asJson,
        "t" -> a.t.filter(_.nonEmpty).asJson
      )
    }
  }

  implicit def decoderPayload: Decoder[Payload[Any]] = new Decoder[Payload[Any]] {

    override def apply(c: HCursor): Result[Payload[Any]] = {
      for {
        opcode <- c.downField("op").as[Int].map(Operation.find[Any](_).get)
        event <- c.downField("t").as[Option[String]].map { opt =>
          opt.flatMap(Event.find[Any]).getOrElse(Event.None)
        }
        id      = Payload.Id(opcode, event)
        decoder = codecs(id).map(_.asInstanceOf[Decoder[Any]])
        data <- if (decoder.isEmpty) Right(None) else c.downField("d").as[Any](decoder.get).map(Option(_))
        seq  <- c.downField("s").as[Option[Int]]
      } yield Payload[Any](opcode.value, data, seq, Option(event.value).filter(_.nonEmpty))
    }
  }

  var codecs: Map[Payload.Id[_, _], Option[Codec[_]]] = Map.empty

  def register(even: Event.Name[Nothing]): Unit = {
    val id = Payload.Id(Operation.Dispatch, even)
    codecs = codecs ++ Map(id -> None)
  }

  def register(opcode: Operation.Code[Nothing]): Unit = {
    val id = Payload.Id(opcode, Event.None)
    codecs = codecs ++ Map(id -> None)
  }

  def register[T: Encoder: Decoder](even: Event.Name[_]): Unit = register[T](Operation.Dispatch, even)

  def register[T: Encoder: Decoder](opcode: Operation.Code[_]): Unit = register[T](opcode, Event.None)

  def register[T: Encoder: Decoder](opcode: Operation.Code[_], event: Event.Name[_]): Unit = {
    val decoder = implicitly[Decoder[T]]
    val encoder = implicitly[Encoder[T]]
    val codec   = Codec.from(decoder, encoder)
    val id      = Payload.Id(opcode, event)
    codecs = codecs ++ Map(id -> Some(codec))
  }

  register[Payload.Hello](Operation.Hello, Event.None)
  register[Payload.Ready](Operation.Dispatch, Event.Ready)
  register[Int](Operation.Heartbeat)
  register(Operation.HeartbeatAck)

  val p1 = Payload(Operation.Hello.value, Some(Payload.Hello(100500)), None, Some(Event.None.value))
  val p2 = Payload(Operation.Dispatch.value, Some(Payload.Ready("xxx-yyy-zzz")), None, Some(Event.Ready.value))
  val p3 = Payload(Operation.Heartbeat.value, Some(500100), None, None)
  val p4 = Payload(Operation.HeartbeatAck.value, None, None, None)

  val json = p4.asJson
  println(json)
  val payload = json.as[Payload[Any]].getOrElse(throw new RuntimeException)
  println(payload)

  processing(payload)

  def processing(payload: Payload[Any]): Unit = payload match {
    case Payload(_, Some(d: Payload.Hello), _, _)               => println(s"Hello: ${d.heartbeatInterval}")
    case Payload(_, Some(d: Payload.Ready), _, _)               => println(s"Ready: ${d.sessionId}")
    case Payload(Operation.Heartbeat.value, Some(d: Int), _, _) => println(s"Heartbeat: $d")
    case Payload(Operation.HeartbeatAck.value, _, _, _)         => println(s"HeartbeatAck")
  }

}
