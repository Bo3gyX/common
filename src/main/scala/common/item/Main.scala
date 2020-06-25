package common.item

import _root_.util.AkkaApp

import scala.collection.mutable

object Main extends AkkaApp("Common-Item") {

  val d1 = Description.Food("apple", 1, 1)
  val d2 = Description.Food("milk", 1, 3)

  val i1 = d1.create
  val i2 = d1.create
  val i3 = d2.create

  println(i1 == i2)
  println(i1 == i3)

  class Stack[I, D <: Stack.DESC[I]](i: Stack.IT[I, D], s: Stack.IT[I, D]*) {
    private val _items: mutable.Buffer[I] = mutable.Buffer(i) ++ s

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

  object Stack {
    type IT[I, D] = I {
      type DESC = D
    }
    type DESC[I] = Description {
      type ITEM = I
    }
  }

  val s1 = new Stack(i1)

  s1.items.foreach { i =>
    println(s"desc: ${i.desc.name}, ${i.portion}")
  }

}
