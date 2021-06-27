package discord

object Messages {

  case class Hello(payload: Entities.Hello)
    extends Communication.Op(Opcode.Hello)
    with Communication.Payload[Entities.Hello]

  case class Heartbeat(payload: Int) extends Communication.Op(Opcode.Heartbeat) with Communication.Payload[Int]

  case object HeartbeatAck extends Communication.Op(Opcode.HeartbeatAck) with Communication.Payload.Empty

  case class Identify(payload: Entities.Identify)
    extends Communication.Op(Opcode.Identify)
    with Communication.Payload[Entities.Identify]

}
