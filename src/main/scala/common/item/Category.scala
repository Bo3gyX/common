package common.item

sealed abstract class Category(val name: String)

object Category {
  case object Food   extends Category("food")
  case object Weapon extends Category("weapon")
  case object Tool   extends Category("tool")

  val values: Seq[Category] = Seq(Food, Weapon, Tool)

  def get(name: String): Category =
    values
      .find(_.name.equalsIgnoreCase(name))
      .getOrElse(
        throw new Exception(s"Category `$name` undefined")
      )
}
