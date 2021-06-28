package discord.entities

case class Attachment(
    id: String,
    filename: String,
    contentType: Option[String],
    size: Int, //integer	size of file in bytes
    url: String,
    proxyUrl: String,
    height: Option[Int],
    width: Option[Int])
