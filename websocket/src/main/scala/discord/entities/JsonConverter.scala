package discord.entities

import discord.Entities._
import discord.JsonSupported
import io.circe.Decoder.Result
import io.circe._
import io.circe.derivation.{deriveDecoder, deriveEncoder, renaming}
import io.circe.syntax.EncoderOps

trait JsonConverter extends JsonSupported {

  implicit val encoderHello: Encoder[Hello] = deriveEncoder[Hello](renaming.snakeCase)
  implicit val decoderHello: Decoder[Hello] = deriveDecoder[Hello](renaming.snakeCase)

  implicit val encoderProperties: Encoder[Properties] = new Encoder[Properties] {

    override def apply(a: Properties): Json = Json.obj(
      "$os" -> a.os.asJson,
      "$browser" -> a.browser.asJson,
      "$device" -> a.device.asJson
    )
  }

  implicit val decoderProperties: Decoder[Properties] = new Decoder[Properties] {

    override def apply(c: HCursor): Result[Properties] =
      for {
        os      <- c.downField("$os").as[String]
        browser <- c.downField("$browser").as[String]
        device  <- c.downField("$device").as[String]
      } yield Properties(os, browser, device)
  }

  implicit val encoderGuildShard: Encoder[GuildShard] = new Encoder[GuildShard] {
    override def apply(a: GuildShard): Json = Json.arr(a.id.asJson, a.num.asJson)
  }

  implicit val decoderGuildShard: Decoder[GuildShard] = new Decoder[GuildShard] {

    override def apply(c: HCursor): Result[GuildShard] =
      for {
        arr <- c.as[Array[Int]]
        res <- arr match {
          case Array(id: Int, num: Int) => Right(GuildShard(id, num))
          case _                        => Left(DecodingFailure("error parse GuildShard", c.history))
        }
      } yield res
  }

  implicit val encoderActivityType: Encoder[ActivityType] = new Encoder[ActivityType] {
    override def apply(a: ActivityType): Json = a.code.asJson
  }

  implicit val decoderActivityType: Decoder[ActivityType] = new Decoder[ActivityType] {

    override def apply(c: HCursor): Result[ActivityType] =
      for {
        code <- c.as[Int]
        res  <- fromValid(c, ActivityType.valid(code))
      } yield res
  }

  implicit val encoderStatusType: Encoder[StatusType] = new Encoder[StatusType] {
    override def apply(a: StatusType): Json = a.code.asJson
  }

  implicit val decoderStatusType: Decoder[StatusType] = new Decoder[StatusType] {

    override def apply(c: HCursor): Result[StatusType] =
      for {
        code <- c.as[String]
        res  <- fromValid(c, StatusType.valid(code))
      } yield res
  }

  implicit val encoderActivity: Encoder[Activity] = deriveEncoder[Activity](renaming.snakeCase)
  implicit val decoderActivity: Decoder[Activity] = deriveDecoder[Activity](renaming.snakeCase)

  implicit val encoderPresenceUpdate: Encoder[PresenceUpdate] = deriveEncoder[PresenceUpdate](renaming.snakeCase)
  implicit val decoderPresenceUpdate: Decoder[PresenceUpdate] = deriveDecoder[PresenceUpdate](renaming.snakeCase)

  implicit val encoderIntent: Encoder[Intent] = new Encoder[Intent] {
    override def apply(a: Intent): Json = a.value.asJson
  }

  implicit val decoderIntent: Decoder[Intent] = new Decoder[Intent] {

    override def apply(c: HCursor): Result[Intent] =
      for {
        code <- c.as[Int]
        res  <- fromValid(c, Intent.valid(code))
      } yield res
  }

  implicit val encoderIdentify: Encoder[Identify] = deriveEncoder[Identify](renaming.snakeCase)
  implicit val decoderIdentify: Decoder[Identify] = deriveDecoder[Identify](renaming.snakeCase)

  implicit val encoderUser: Encoder[User] = deriveEncoder[User](renaming.snakeCase)
  implicit val decoderUser: Decoder[User] = deriveDecoder[User](renaming.snakeCase)

  implicit val encoderReady: Encoder[Ready] = deriveEncoder[Ready](renaming.snakeCase)
  implicit val decoderReady: Decoder[Ready] = deriveDecoder[Ready](renaming.snakeCase)

}

object JsonConverter extends JsonConverter
