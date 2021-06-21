package discord

object Messages {

  case class Hello(payload: Entities.Hello) extends Message.Op(Opcode.Hello) with Message.Payload[Entities.Hello]
  case class Heartbeat(payload: Int)        extends Message.Op(Opcode.Heartbeat) with Message.Payload[Int]
  case object HeartbeatAck                  extends Message.Op(Opcode.HeartbeatAck) with Message.Payload.Empty

  case class Identify(payload: Entities.Identify)
    extends Message.Op(Opcode.Identify)
    with Message.Payload[Entities.Identify]
}
