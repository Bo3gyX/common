package discord

trait Communication {
  type Op <: Opcode
  type Payload
  def op: Op
  def d: Option[Payload]
  def s: Option[Int]
  def t: Option[String]
}

object Communication {

  abstract class Op[Op0 <: Opcode](val op: Op0) extends Communication {
    override type Op = Op0
    override def s: Option[Int]    = None
    override def t: Option[String] = None
  }

  trait Payload[T] { self: Communication =>
    override type Payload = T
    def payload: T
    override def d: Option[T] = Some(payload)
  }

  object Payload {

    trait Empty extends { self: Communication =>
      override type Payload = Nothing
      override def d: Option[Nothing] = None
    }
  }
}
