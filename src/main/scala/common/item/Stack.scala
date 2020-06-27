package common.item

import scala.collection.mutable

class Stack[I <: Item with Item.Stackable](i: I) {
  private val _items: mutable.Buffer[I] = mutable.Buffer(i)

  def items: List[I] = _items.toList

  def add(i: I*): this.type = {
    _items.appendAll(i)
    this
  }

  def remove(count: Int): this.type = {
    _items.remove(0, count)
    this
  }
}