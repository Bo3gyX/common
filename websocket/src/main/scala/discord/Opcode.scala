package discord

sealed abstract class Opcode(val code: Int)

object Opcode extends Enum[Int, Opcode] {
  case object Hello        extends Opcode(10)
  case object Heartbeat    extends Opcode(1)
  case object HeartbeatAck extends Opcode(11)
  case object Identify     extends Opcode(2)

  override def values: Seq[Opcode] = Seq(Hello, Heartbeat, HeartbeatAck, Identify)

  override def find(value: Int): Option[Opcode] = values.find(_.code == value)
}
