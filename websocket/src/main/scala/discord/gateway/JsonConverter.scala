package discord.gateway

import io.circe.Decoder.Result
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, DecodingFailure, Encoder, HCursor, Json}

trait JsonConverter extends discord.entities.JsonConverter {

  implicit val codecOperationCode: Codec[Operation.Code[Any]] = new Codec[Operation.Code[Any]] {

    override def apply(c: HCursor): Result[Operation.Code[Any]] =
      for {
        value <- c.as[Int]
        code  <- Operation.find(value).toRight(DecodingFailure(s"Undefined code $value", c.history))
      } yield code

    override def apply(a: Operation.Code[Any]): Json = a.value.asJson
  }

  implicit val codeEventName: Codec[Event.Name[Any]] = new Codec[Event.Name[Any]] {

    override def apply(c: HCursor): Result[Event.Name[Any]] =
      for {
        value <- c.as[Option[String]]
        name <- value match {
          case Some(v) => Event.find(v).toRight(DecodingFailure(s"Undefined name $v", c.history))
          case None    => Right(Event.None)
        }
      } yield name

    override def apply(a: Event.Name[Any]): Json =
      (a match {
        case Event.None => None
        case name       => Some(name.value)
      }).asJson
  }

  implicit val encoderPayload: Encoder[Payload] = new Encoder[Payload] {

    override def apply(a: Payload): Json = {
      val opcode: Operation.Code[Any] = a.opcode
      val event: Event.Name[Any]      = a.event

      val id = Payload.Id(opcode, event)
      val codec = codecs
        .get(id)
        .map(_.map(_.asInstanceOf[Encoder[a.T]]))
        .getOrElse(
          throw new NoSuchElementException(id.toString)
        )

      val data = for {
        c <- codec
        d <- a.data
      } yield d.asJson(c)

      Json.obj(
        "op" -> opcode.asJson,
        "d" -> data.asJson,
        "s" -> a.s.asJson,
        "t" -> event.asJson
      )
    }
  }

  implicit val decoderPayload: Decoder[Payload] = new Decoder[Payload] {

    override def apply(c: HCursor): Result[Payload] = {
      for {
        opcode <- c.downField("op").as[Operation.Code[Any]]
        event  <- c.downField("t").as[Event.Name[Any]]
        id = Payload.Id(opcode, event)
        decoder = codecs
          .get(id)
          .map(_.map(_.asInstanceOf[Decoder[Any]]))
          .getOrElse(
            throw new NoSuchElementException(id.toString)
          )
        data <- if (decoder.isEmpty) Right(None) else c.downField("d").as[Any](decoder.get).map(Option(_))
        seq  <- c.downField("s").as[Option[Int]]
      } yield Payload.Raw[Any](opcode, data, seq, event)
    }
  }

  var codecs: Map[Payload.Id, Option[Codec[_]]] = Map.empty

  def register(even: Event.Name[Nothing]): Unit = {
    val id = Payload.Id(Operation.Dispatch, even)
    codecs = codecs ++ Map(id -> None)
  }

  def register(opcode: Operation.Code[Nothing]): Unit = {
    val id = Payload.Id(opcode, Event.None)
    codecs = codecs ++ Map(id -> None)
  }

  def register[T: Encoder: Decoder](even: Event.Name[T]): Unit = register[T](Operation.Dispatch, even)

  def register[T: Encoder: Decoder](opcode: Operation.Code[T]): Unit = register[T](opcode, Event.None)

  def register[T: Encoder: Decoder](opcode: Operation.Code[T], event: Event.Name[T]): Unit = {
    val decoder = implicitly[Decoder[T]]
    val encoder = implicitly[Encoder[T]]
    val codec   = Codec.from(decoder, encoder)
    val id      = Payload.Id(opcode, event)
    codecs = codecs ++ Map(id -> Some(codec))
  }
}
