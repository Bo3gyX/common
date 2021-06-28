package discord.entities

import discord.Enum
import discord.entities.Embed._

import java.time.Instant

case class Embed(
    title: Option[String],
    `type`: Option[EmbedType],
    description: Option[String],
    url: Option[String],
    timestamp: Option[Instant],
    color: Option[Int],
    footer: Option[EmbedFooter],
    image: Option[EmbedImage],
    thumbnail: Option[EmbedThumbnail],
    video: Option[EmbedVideo],
    provider: Option[EmbedProvider],
    author: Option[EmbedAuthor],
    fields: Option[EmbedField])

object Embed {
  sealed abstract class EmbedType(val value: String)

  object EmbedType extends Enum[String, EmbedType] {
    case object Rich    extends EmbedType("rich")
    case object Image   extends EmbedType("image")
    case object Video   extends EmbedType("video")
    case object Gifv    extends EmbedType("gifv")
    case object Article extends EmbedType("article")
    case object Link    extends EmbedType("link")

    override def values: Seq[EmbedType]                 = Seq(Rich, Image, Video, Gifv, Article, Link)
    override def find(value: String): Option[EmbedType] = values.find(_.value == value)
  }

  case class EmbedFooter(text: String, iconUrl: Option[String], proxyIconUrl: Option[String])
  case class EmbedImage(url: Option[String], proxyUrl: Option[String], height: Option[Int], width: Option[Int])
  case class EmbedThumbnail(url: Option[String], proxyUrl: Option[String], height: Option[Int], width: Option[Int])
  case class EmbedVideo(url: Option[String], proxyUrl: Option[String], height: Option[Int], width: Option[Int])
  case class EmbedProvider(name: Option[String], url: Option[String])

  case class EmbedAuthor(
      name: Option[String],
      url: Option[String],
      iconUrl: Option[String],
      proxyIconUrl: Option[String])

  case class EmbedField(name: String, value: String, inline: Option[Boolean])

}
