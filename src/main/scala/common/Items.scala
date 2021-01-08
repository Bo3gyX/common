package common

object Items {

  trait Category {
    def id: String
  }

  trait Description {
    def id: String
    def category: Category
  }

  trait Item {
    def desc: Description
    def category: Category
  }

  trait BoxDescription extends Description {
    def size: Int
  }

  case class Stack(size: Int, items: Seq[Item]) {
    require(items.nonEmpty, "required non empty items")
    require(items.size <= size, s"required max items $size")

    val desc: Description = items.head.desc

    def add(item: Item): Stack = {
      if (item.desc.id != desc.id) {
        throw new IndexOutOfBoundsException(s"Invalid entity type item `${item.desc.id}`")
      }
      if (items.size >= this.size) {
        throw new IndexOutOfBoundsException(s"There is not enough space for stack's item `${item.desc.id}`")
      }
      this.copy(items = item +: this.items)
    }

    def canAdd(item: Item): Boolean = {
      item.desc.id == desc.id && items.size < this.size
    }

    def merge(stack: Stack): Stack = {
      if (!checkId(stack)) {
        throw new IndexOutOfBoundsException(s"Invalid entity type items `${stack.desc.id}`")
      }

      if (!checkSize(stack)) {
        throw new IndexOutOfBoundsException(s"There is not enough space for stack's items `${stack.desc.id}`")
      }

      this.copy(items = this.items ++ stack.items)
    }

    def canMerge(stack: Stack): Boolean = {
      checkId(stack) && checkSize(stack)
    }

    private def checkId(stack2: Stack): Boolean =
      this.desc.id == stack2.desc.id

    private def checkSize(stack: Stack): Boolean = {
      val summarySize = this.items.size + stack.items.size
      summarySize <= this.size
    }
  }

  case class Box(stacks: Map[Int, Stack], desc: BoxDescription) extends Item {
    override def category: Category = desc.category

    def add(idx: Int, stack: Stack): Box = {
      if (idx >= 0 && idx < this.desc.size) {
        val newStack = stacks.get(idx) match {
          case Some(curStack) if curStack.canMerge(stack) => curStack.merge(stack)
          case None if stacks.size < this.desc.size       => stack
          case _                                          => throw new IndexOutOfBoundsException(idx)
        }
        this.copy(stacks = stacks ++ Map(idx -> newStack))
      } else throw new IndexOutOfBoundsException(idx)
    }

    def canAdd(idx: Int, stack: Stack): Boolean = {
      if (idx >= 0 && idx < this.desc.size) {
        val found = stacks.get(idx)
        if (found.isEmpty) stacks.size < this.desc.size
        else found.exists(_.canMerge(stack))
      } else false
    }
  }

  case object Food extends Category {
    override def id: String = "food"
  }

  case object Tool extends Category {
    override def id: String = "tool"
  }

  case object Box extends Category {
    override def id: String = "box"
  }

  case class FoodDescription(id: String) extends Description {
    override def category: Category = Food
  }

  case class ToolDescription(id: String) extends Description {
    override def category: Category = Tool
  }

  case class DefBoxDescription(id: String, size: Int) extends BoxDescription {
    override def category: Category = Box
  }

  case class DefItem(desc: Description) extends Item {
    override def category: Category = desc.category
  }

  val descFood1 = FoodDescription("apple")
  val descFood2 = FoodDescription("orange")
  val descTool1 = ToolDescription("hammer")
  val descTool2 = ToolDescription("screwdriver")

  val descBox1 = DefBoxDescription("small_bag", 3)
  val descBox2 = DefBoxDescription("big_bag", 10)

  val itemsFood1 = Seq(DefItem(descFood1), DefItem(descFood1))
  val itemsFood2 = Seq(DefItem(descFood2))
  val itemsTool1 = Seq(DefItem(descTool1))
  val itemsTool2 = Seq(DefItem(descTool2))

  val stackFood1 = Stack(2, itemsFood1)
  val stackFood2 = Stack(3, itemsFood2)
  val stackTool1 = Stack(1, itemsTool1)
  val stackTool2 = Stack(1, itemsTool2)

  //  println(stack1.merge(stack2))

  val box  = Box(Map.empty, descBox1)
  val box1 = box.add(0, stackFood1).add(2, stackFood2)
  print(box1)
  val box2 = box1.add(1, stackTool1).add(2, stackFood2)
  print(box2)
  val box3 = box2.add(2, stackFood2)
  print(box3)

  def print(box: Box): Unit = {
    println(s"--- box size: ${box.stacks.size} / ${box.desc.size}")

    for ((idx, stack) <- box.stacks.toSeq.sortBy(_._1)) {
      println(s" $idx  | ${stack.desc.category.id} | ${stack.desc.id} | ${stack.items.size} / ${stack.size}")
    }
  }

}
