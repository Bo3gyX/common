package websocket

import discord.gateway.{Event, Operation}
import discord.{entities, _}
import io.circe.derivation.renaming
import io.circe.syntax.EncoderOps
import io.circe._
import util.Application

object Test extends Application with entities.JsonConverter with gateway.JsonConverter {

  case class Payload3(op: Operation.Code[_], d: Option[Json], s: Option[Int], t: Option[Event.Name[_]])

  object Payload3 {

    trait Data[T] extends { this: Payload3 =>
      def codec: Codec[T]

      def apply(payload: T): Payload3 =
        Payload3(op, Some(payload.asJson(codec)), None, t)

      def unapply(payload: Payload3): Option[T] =
        payload.d.map(_.as[T](codec)).flatMap(_.toOption)
    }

    def message(op: Operation.Code[Nothing], ent: Option[Event.Name[Nothing]]): Payload3 = Payload3(op, None, None, ent)

    def message[T: Decoder: Encoder](
        op: Operation.Code[T],
        ent: Option[Event.Name[T]]): Payload3 with Payload3.Data[T] =
      new Payload3(op, None, None, ent) with Payload3.Data[T] {

        override def codec: Codec[T] = {
          val decoder = implicitly[Decoder[T]]
          val encoder = implicitly[Encoder[T]]
          Codec.from(decoder, encoder)
        }
      }

    def operation(op: Operation.Code[Nothing]): Payload3                             = message(op, None)
    def operation[T: Decoder: Encoder](op: Operation.Code[T]): Payload3 with Data[T] = message[T](op, None)

    def event(ent: Event.Name[Nothing]): Payload3 = message(Operation.Dispatch, Some(ent))

    def event[T: Decoder: Encoder](ent: Event.Name[T]): Payload3 with Data[T] =
      message[T](Operation.Dispatch, Some(ent))
  }

  val Hello        = Payload3.operation(Operation.Hello)
  val Heartbeat    = Payload3.operation(Operation.Heartbeat)
  val HeartbeatAck = Payload3.operation(Operation.HeartbeatAck)
  val Ready        = Payload3.event(Event.Ready)

  implicit val codecPayload3: Codec[Payload3] = derivation.deriveCodec[Payload3](renaming.snakeCase)

  def test(payload: Payload3): Unit = {
    println(s"in: $payload")
    payload match {
//      case Payload3(op, d, s, e) => println(s"Raw: $op, $d, $s, $e")

      case raw @ Hello(payload) =>
        println(s"Hello: ${payload.heartbeatInterval}")
        println(s"Raw: ${raw.op}, ${raw.d}, ${raw.s}, ${raw.t}")

      case Heartbeat(payload) => println(s"Heartbeat: $payload")
      case HeartbeatAck       => println(s"HeartbeatAck")
      case x                  => println(s"Unsupported $x")
    }
  }

  val p1 = Hello(entities.Hello(10500))
  val p2 = Heartbeat(100500)
  val p3 = HeartbeatAck
  val p4 = Payload3(Operation.Heartbeat, Some(400.asJson), Some(2), None)

  superTest(p1)
  superTest(p2)
  superTest(p3)
  superTest(p4)

  def superTest(p: Payload3): Unit = {
    println(">---------------------------------------------")
    val js = p.asJson.dropNullValues.noSpaces
    println(js)
    val res = parser.parse(js).flatMap(_.as[Payload3])
    println(res)
    val obj = res.getOrElse(throw new Exception("parse error!!!"))
    println(obj)
    test(obj)
    println("---------------------------------------------<")
  }
//  test(p1)
//  test(p2)
//  test(p3)
//  test(p4)
}
