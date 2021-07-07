package discord.gateway

trait Payload {
  type T
  def opcode: Operation.Code[T]
  def data: Option[T]
  def s: Option[Int]
  def event: Event.Name[T]
}

object Payload {
  case class Id(opcode: Operation.Code[Any], event: Event.Name[Any])

  case class Raw[T0](opcode: Operation.Code[T0], data: Option[T0], s: Option[Int], event: Event.Name[T0])
    extends Payload { override type T = T0 }

  def apply(opcode: Operation.Code[Nothing]): Payload =
    Raw(opcode, None, None, Event.None)

  def apply[T](opcode: Operation.Code[T], entity: T): Payload =
    Raw(opcode, Some(entity), None, Event.None)

  def apply[T](event: Event.Name[T], entity: T): Payload =
    Raw(Operation.Dispatch, Some(entity), None, event)
}
