package common.item

sealed abstract class Description(val category: Category) { self =>
  type CAT <: Category
  type ITEM <: InnerItem

  trait InnerItem extends Item {
    override type DESC = self.type
    override def desc: DESC = self
  }

  def name: String
  def create: ITEM
}

object Description {

  case class Food(name: String, portion: Int, restore: Int) extends Description(Category.Food) {
    override type CAT  = Category.Food.type
    override type ITEM = Food
    case class Food(portion: Int) extends InnerItem with Item.Stackable
    override def create: ITEM = Food(portion)
  }

  case class Weapon(name: String, damage: Int, rate: Int, capacity: Int) extends Description(Category.Weapon) {
    override type CAT  = Category.Weapon.type
    override type ITEM = Weapon
    case class Weapon(damage: Int, ammo: Int) extends InnerItem
    override def create: ITEM = Weapon(damage, capacity)
  }

  case class Tool(name: String, repair: Int, durability: Int) extends Description(Category.Tool) {
    override type CAT  = Category.Tool.type
    override type ITEM = Tool
    case class Tool(durability: Int) extends InnerItem
    override def create: ITEM = Tool(durability)
  }

}
