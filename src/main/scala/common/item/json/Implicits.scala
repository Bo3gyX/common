package common.item.json

import common.item.{Category, Description}
import play.api.libs.json._

import scala.util.Try

trait Implicits {

  implicit val jsonCategory: Format[Category] = new Format[Category] {

    override def reads(json: JsValue): JsResult[Category] = {
      for {
        name <- json.validate[String]
        res  <- JsResult.fromTry(Try(Category.get(name)))
      } yield res
    }

    override def writes(o: Category): JsValue = {
      JsString(o.name)
    }
  }

  implicit val jsonReadsDesc: Format[Description] = new Format[Description] {

    private val registry: Registry.Finalized = new Registry.Open {
      register(Category.Food, Json.format[Description.Food])
      register(Category.Weapon, Json.format[Description.Weapon])
      register(Category.Tool, Json.format[Description.Tool])
    }.finale()

    private case class Raw(category: Category, data: JsValue)
    implicit private val jsonRaw: Format[Raw] = Json.format[Raw]

    override def reads(json: JsValue): JsResult[Description] = {
      for {
        raw  <- json.validate[Raw]
        desc <- registry.readers(raw.category)(raw.data)
      } yield desc
    }

    override def writes(o: Description): JsValue = {
      Json.toJson(Raw(o.category, registry.writers(o.category)(o)))
    }
  }
}

object Implicits extends Implicits
