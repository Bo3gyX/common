package util

trait Queue[T] {
  def appended(e: T): this.type
  def dequeue: T
  def isEmpty: Boolean
  def nonEmpty: Boolean
  def size: Int
}
