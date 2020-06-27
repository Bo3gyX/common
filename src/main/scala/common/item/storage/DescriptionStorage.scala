package common.item.storage

import common.item.json.Implicits
import common.item.{Description, Raw}
import play.api.libs.json.Json

import scala.reflect.ClassTag

trait DescriptionStorage {
  def foods: Map[String, Description.Food]
  def weapons: Map[String, Description.Weapon]
  def tools: Map[String, Description.Tool]
}

object DescriptionStorage extends DescriptionStorage with Implicits {
  lazy val data: Seq[Description] = Json.parse(Raw.descriptions).as[Seq[Description]]

  val foods: Map[String, Description.Food]     = collectMap[Description.Food]
  val weapons: Map[String, Description.Weapon] = collectMap[Description.Weapon]
  val tools: Map[String, Description.Tool]     = collectMap[Description.Tool]

  private def collectMap[D <: Description: ClassTag]: Map[String, D] = {
    data.collect { case d: D => d }.map(d => d.name -> d).toMap
  }
}
