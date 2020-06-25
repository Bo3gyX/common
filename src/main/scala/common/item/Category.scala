package common.item

sealed abstract class Category(val name: String)

object Category {
  case object Food   extends Category("food")
  case object Weapon extends Category("weapon")
  case object Tool   extends Category("tool")
}
