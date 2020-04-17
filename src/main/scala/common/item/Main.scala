package common.item

import _root_.util.AkkaApp
import play.api.libs.json._

object Main extends AkkaApp("Common-Item") {

  val json =
    """
      |[{
      | "type": "food",
      | "name": "apple",
      | "desc": {
      |   "value": 5
      | }
      |},
      |{
      | "type": "food",
      | "name": "egg",
      | "desc": {
      |   "value": 6
      | }
      |},
      |{
      | "type": "tool",
      | "name": "wrench",
      | "desc": {
      |   "durability": 5,
      |   "productivity": 3
      | }
      |},
      |{
      | "type": "weapon",
      | "name": "ak47",
      | "desc": {
      |   "durability": 10,
      |   "damage": 10
      | }
      |}]
      |""".stripMargin

  trait ItemProperties

  object ItemProperties {

    trait Durability extends ItemProperties {
      def durability: Int
    }

    trait Productivity extends ItemProperties {
      def productivity: Int
    }

    trait Damage extends ItemProperties {
      def damage: Int
    }

    trait Eatable extends ItemProperties {
      def value: Int
    }
  }

  abstract class ItemType(val name: String)

  object ItemType {
    case object Food   extends ItemType("food")
    case object Tool   extends ItemType("tool")
    case object Weapon extends ItemType("weapon")

    val values: Seq[ItemType] = Seq(Food, Tool, Weapon)

    def get(name: String): ItemType =
      unapply(name).getOrElse(throw new Exception(s"ItemType $name not defined"))

    def unapply(name: String): Option[ItemType] = values.find(_.name == name)

  }

  abstract class ItemDescription[T <: ItemType](val itemType: T)

  object ItemDescription {

    case class Food(value: Int) extends ItemDescription(ItemType.Food) with ItemProperties.Eatable

    case class Tool(durability: Int, productivity: Int)
      extends ItemDescription(ItemType.Tool)
      with ItemProperties.Durability
      with ItemProperties.Productivity

    case class Weapon(durability: Int, damage: Int)
      extends ItemDescription(ItemType.Weapon)
      with ItemProperties.Durability
      with ItemProperties.Damage
  }

  implicit val jsonItemType: Format[ItemType] = new Format[ItemType] {
    override def writes(o: ItemType): JsValue             = JsString(o.name)
    override def reads(json: JsValue): JsResult[ItemType] = json.validate[String].map(ItemType.get)
  }

  implicit val jsonItemDescriptionFood: Format[ItemDescription.Food]     = Json.format[ItemDescription.Food]
  implicit val jsonItemDescriptionTool: Format[ItemDescription.Tool]     = Json.format[ItemDescription.Tool]
  implicit val jsonItemDescriptionWeapon: Format[ItemDescription.Weapon] = Json.format[ItemDescription.Weapon]

  val desc1 = ItemDescription.Food(1000)
  val desc2 = ItemDescription.Tool(100, 3)
  val desc3 = ItemDescription.Weapon(70, 10)

}
