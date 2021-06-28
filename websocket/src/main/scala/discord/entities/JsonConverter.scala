package discord.entities

import discord.JsonSupported
import discord.entities.Activity.ActivityType
import discord.entities.Embed.{EmbedAuthor, EmbedField, EmbedFooter, EmbedImage, EmbedProvider, EmbedThumbnail, EmbedType, EmbedVideo}
import discord.entities.Message.{MessageActivity, MessageActivityType, MessageReference, MessageType}
import discord.entities.Role.RoleTags
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

  implicit val encoderGuildMember: Encoder[GuildMember] = deriveEncoder[GuildMember](renaming.snakeCase)
  implicit val decoderGuildMember: Decoder[GuildMember] = deriveDecoder[GuildMember](renaming.snakeCase)

  implicit val encoderReady: Encoder[Ready] = deriveEncoder[Ready](renaming.snakeCase)
  implicit val decoderReady: Decoder[Ready] = deriveDecoder[Ready](renaming.snakeCase)

  implicit val encoderRoleTags: Encoder[RoleTags] = deriveEncoder[RoleTags](renaming.snakeCase)
  implicit val decoderRoleTags: Decoder[RoleTags] = deriveDecoder[RoleTags](renaming.snakeCase)

  implicit val encoderRole: Encoder[Role] = deriveEncoder[Role](renaming.snakeCase)
  implicit val decoderRole: Decoder[Role] = deriveDecoder[Role](renaming.snakeCase)

  implicit val encoderChannelMention: Encoder[ChannelMention] = deriveEncoder[ChannelMention](renaming.snakeCase)
  implicit val decoderChannelMention: Decoder[ChannelMention] = deriveDecoder[ChannelMention](renaming.snakeCase)

  implicit val encoderAttachment: Encoder[Attachment] = deriveEncoder[Attachment](renaming.snakeCase)
  implicit val decoderAttachment: Decoder[Attachment] = deriveDecoder[Attachment](renaming.snakeCase)

  implicit val encoderEmbedType: Encoder[EmbedType] = new Encoder[EmbedType] {
    override def apply(a: EmbedType): Json = a.value.asJson
  }

  implicit val decoderEmbedType: Decoder[EmbedType] = new Decoder[EmbedType] {

    override def apply(c: HCursor): Result[EmbedType] =
      for {
        value <- c.as[String]
        res   <- fromValid(c, EmbedType.valid(value))
      } yield res
  }

  implicit val encoderEmbedFooter: Encoder[EmbedFooter] = deriveEncoder[EmbedFooter](renaming.snakeCase)
  implicit val decoderEmbedFooter: Decoder[EmbedFooter] = deriveDecoder[EmbedFooter](renaming.snakeCase)

  implicit val encoderEmbedImage: Encoder[EmbedImage] = deriveEncoder[EmbedImage](renaming.snakeCase)
  implicit val decoderEmbedImage: Decoder[EmbedImage] = deriveDecoder[EmbedImage](renaming.snakeCase)

  implicit val encoderEmbedThumbnail: Encoder[EmbedThumbnail] = deriveEncoder[EmbedThumbnail](renaming.snakeCase)
  implicit val decoderEmbedThumbnail: Decoder[EmbedThumbnail] = deriveDecoder[EmbedThumbnail](renaming.snakeCase)

  implicit val encoderEmbedVideo: Encoder[EmbedVideo] = deriveEncoder[EmbedVideo](renaming.snakeCase)
  implicit val decoderEmbedVideo: Decoder[EmbedVideo] = deriveDecoder[EmbedVideo](renaming.snakeCase)

  implicit val encoderEmbedProvider: Encoder[EmbedProvider] = deriveEncoder[EmbedProvider](renaming.snakeCase)
  implicit val decoderEmbedProvider: Decoder[EmbedProvider] = deriveDecoder[EmbedProvider](renaming.snakeCase)

  implicit val encoderEmbedAuthor: Encoder[EmbedAuthor] = deriveEncoder[EmbedAuthor](renaming.snakeCase)
  implicit val decoderEmbedAuthor: Decoder[EmbedAuthor] = deriveDecoder[EmbedAuthor](renaming.snakeCase)

  implicit val encoderEmbedField: Encoder[EmbedField] = deriveEncoder[EmbedField](renaming.snakeCase)
  implicit val decoderEmbedField: Decoder[EmbedField] = deriveDecoder[EmbedField](renaming.snakeCase)

  implicit val encoderEmbed: Encoder[Embed] = deriveEncoder[Embed](renaming.snakeCase)
  implicit val decoderEmbed: Decoder[Embed] = deriveDecoder[Embed](renaming.snakeCase)

  implicit val encoderEmoji: Encoder[Emoji] = deriveEncoder[Emoji](renaming.snakeCase)
  implicit val decoderEmoji: Decoder[Emoji] = deriveDecoder[Emoji](renaming.snakeCase)

  implicit val encoderReaction: Encoder[Reaction] = deriveEncoder[Reaction](renaming.snakeCase)
  implicit val decoderReaction: Decoder[Reaction] = deriveDecoder[Reaction](renaming.snakeCase)

  implicit val encoderMessageType: Encoder[MessageType] = new Encoder[MessageType] {
    override def apply(a: MessageType): Json = a.value.asJson
  }

  implicit val decoderMessageType: Decoder[MessageType] = new Decoder[MessageType] {

    override def apply(c: HCursor): Result[MessageType] =
      for {
        value <- c.as[Int]
        res   <- fromValid(c, MessageType.valid(value))
      } yield res
  }

  implicit val encoderMessageActivityType: Encoder[MessageActivityType] = new Encoder[MessageActivityType] {
    override def apply(a: MessageActivityType): Json = a.value.asJson
  }

  implicit val decoderMessageActivityType: Decoder[MessageActivityType] = new Decoder[MessageActivityType] {

    override def apply(c: HCursor): Result[MessageActivityType] =
      for {
        value <- c.as[Int]
        res   <- fromValid(c, MessageActivityType.valid(value))
      } yield res
  }

  implicit val encoderMessageActivity: Encoder[MessageActivity] = deriveEncoder[MessageActivity](renaming.snakeCase)
  implicit val decoderMessageActivity: Decoder[MessageActivity] = deriveDecoder[MessageActivity](renaming.snakeCase)

  implicit val encoderMessageReference: Encoder[MessageReference] = deriveEncoder[MessageReference](renaming.snakeCase)
  implicit val decoderMessageReference: Decoder[MessageReference] = deriveDecoder[MessageReference](renaming.snakeCase)

  implicit val encoderMessage: Encoder[Message] = deriveEncoder[Message](renaming.snakeCase)
  implicit val decoderMessage: Decoder[Message] = deriveDecoder[Message](renaming.snakeCase)
}

object JsonConverter extends JsonConverter
