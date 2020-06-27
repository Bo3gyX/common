package common.item

trait Item {
  type DESC <: Description
  def desc: DESC
}

object Item {
  trait Stackable
}
